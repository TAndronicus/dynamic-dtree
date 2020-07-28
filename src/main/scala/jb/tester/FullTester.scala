package jb.tester

import jb.conf.Config
import jb.util.Const.{FEATURES, LABEL, PREDICTION}
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.sql.DataFrame

object FullTester {

  def testMv(testSubset: DataFrame, nClassif: Int): (Double, Double, Double, Double) = {
    val cols = for (i <- 0.until(nClassif)) yield PREDICTION + "_" + i
    val mvLabels = testSubset.select(cols.head, cols.takeRight(cols.length - 1): _*).collect()
      .map(row => row.toSeq.groupBy(_.asInstanceOf[Double].doubleValue()).mapValues(_.length).reduce((t1, t2) => if (t1._2 > t2._2) t1 else t2)).map(_._1)
    val refLabels = getReferenceLabels(testSubset)
    calculateStatistics(mvLabels, refLabels)
  }

  private def getReferenceLabels(testedSubset: DataFrame): Array[Double] = {
    testedSubset.select(LABEL).collect().map(_.get(0)).map {
      case int: Int => int.toDouble
      case double: Double => double
    }
  }

  def fScore(precission: Double, recall: Double, beta: Double) = (math.pow(beta, 2) + 1) * precission * recall / (math.pow(beta, 2) * precission + recall)

  private def calculateStatistics(predLabels: Array[Double], refLabels: Array[Double]): (Double, Double, Double, Double) = {
    val before = System.currentTimeMillis()
    val allIndexesSize = refLabels.size.toDouble
    val allLabels = refLabels.distinct
    val indexMap = allLabels.map(
      label => (
        refLabels.zipWithIndex.collect { case (l, i) if (l == label) => i }.toSet,
        predLabels.zipWithIndex.collect { case (l, i) if (l == label) => i }.toSet
      )
    )
    val acc = indexMap
      .map { case (ref, pred) => (allIndexesSize - ((ref ++ pred).size - (ref & pred).size)) / allIndexesSize }
      .sum / allLabels.size
    val precissionMi = indexMap
      .map { case (ref, pred) => (ref & pred).size }
      .sum.toDouble / indexMap
      .map { case (_, pred) => pred.size }
      .sum
    val recallMi = indexMap
      .map { case (ref, pred) => (ref & pred).size }
      .sum.toDouble / indexMap
      .map { case (ref, _) => ref.size }
      .sum
    val precissionM = indexMap
      .map { case (ref, pred) => (ref & pred).size.toDouble / pred.size }
      .filter(!_.isNaN)
      .sum / allLabels.size
    val recallM = indexMap
      .map { case (ref, pred) => (ref & pred).size.toDouble / ref.size }
      .filter(!_.isNaN)
      .sum / allLabels.size
    println(s"Evaluated in ${System.currentTimeMillis() - before}")
    println(s"ACC: $acc,\tpMi: $precissionMi,\trMi: $recallMi,\tpM: $precissionM,\trM: $recallM,\tfSMi: ${fScore(precissionMi, recallMi, 1)},\tfSM: ${fScore(precissionM, recallM, 1)}")
    (precissionMi, recallMi, precissionM, recallM)
  }

  def testI(predictions: Array[Double], testSubset: DataFrame): (Double, Double, Double, Double) = {
    val refLabels = getReferenceLabels(testSubset)
    calculateStatistics(predictions, refLabels)
  }

  def testRF(trainingSubset: DataFrame, testSubset: DataFrame, nClassif: Int): (Double, Double, Double, Double) = {
    trainingSubset.cache()
    val predictions = new RandomForestClassifier()
      .setFeatureSubsetStrategy("auto")
      .setImpurity(Config.impurity)
      .setNumTrees(nClassif)
      .setMaxDepth(Config.maxDepth)
      .setFeaturesCol(FEATURES)
      .setLabelCol(LABEL)
      .fit(trainingSubset)
      .transform(testSubset)
      .select(PREDICTION)
      .collect()
      .toSeq
      .map(a => a.get(0).asInstanceOf[Double])
      .toArray
    trainingSubset.unpersist()
    val reference = getReferenceLabels(testSubset)
    calculateStatistics(predictions, reference)
  }

}
