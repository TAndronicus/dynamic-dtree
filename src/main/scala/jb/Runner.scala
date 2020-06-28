package jb

import java.util.stream.IntStream

import jb.conf.Config
import jb.io.FileReader.getRawInput
import jb.parser.TreeParser
import jb.prediction.Predictions.predictBaseClfs
import jb.selector.FeatureSelectors
import jb.server.SparkEmbedded
import jb.tester.FullTester.{testI, testMv, testRF}
import jb.util.Util._
import jb.vectorizer.FeatureVectorizers.getFeatureVectorizer
import org.apache.spark.ml.Pipeline

class Runner(val nClassif: Int, var nFeatures: Int) {

  def calculateMvIScores(filename: String): Array[Double] = {

    //    import SparkEmbedded.ss.implicits._
    SparkEmbedded.ss.sqlContext.clearCache()

    var input = getRawInput(filename, "csv")
    if (nFeatures > input.columns.length - 1) {
      this.nFeatures = input.columns.length - 1
      println(s"Setting nFeatures to $nFeatures")
    }
    val featureVectorizer = getFeatureVectorizer(input.columns)
    val featureSelector = FeatureSelectors.get_chi_sq_selector(nFeatures)
    val dataPrepPipeline = new Pipeline().setStages(Array(featureVectorizer, featureSelector))
    val dataPrepModel = dataPrepPipeline.fit(input)
    input = optimizeInput(input, dataPrepModel)

    chackExtrema(input, getSelectedFeatures(dataPrepModel))

    val nSubsets = nClassif + 1
    val subsets = input.randomSplit(IntStream.range(0, nSubsets).mapToDouble(_ => 1D / nSubsets).toArray)
    recacheInput2Subsets(input, subsets)
    val (trainingSubsets, testSubset) = dispenseSubsets(subsets)
    val trainingSubset = unionSubsets(trainingSubsets)

    val baseModels = trainingSubsets.map(subset => getEmptyDT.fit(subset))

    val testedSubset = predictBaseClfs(baseModels, testSubset)
    val mvQualityMeasure = testMv(testedSubset, nClassif)
    val rfQualityMeasure = testRF(trainingSubset, testSubset, nClassif)

    val integratedModel = new TreeParser(
      Config.metricFunction,
      Config.mappingFunction
    ).composeTree(baseModels.toList)
    integratedModel.checkDiversity(filename)

    val iPredictions = integratedModel.transform(testedSubset)
    val iQualityMeasure = testI(iPredictions, testedSubset)

    clearCache(subsets)

    Array(mvQualityMeasure._1, if (mvQualityMeasure._2.isNaN) 0D else mvQualityMeasure._2, mvQualityMeasure._3, mvQualityMeasure._4,
      rfQualityMeasure._1, if (rfQualityMeasure._2.isNaN) 0D else rfQualityMeasure._2, rfQualityMeasure._3, rfQualityMeasure._4,
      iQualityMeasure._1, if (iQualityMeasure._2.isNaN) 0D else iQualityMeasure._2, iQualityMeasure._3, iQualityMeasure._4)

  }

}
