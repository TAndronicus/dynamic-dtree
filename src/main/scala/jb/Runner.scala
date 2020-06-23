package jb

import java.util.stream.IntStream

import jb.conf.Config
import jb.io.FileReader.getRawInput
import jb.model._
import jb.model.dt.{CombinedIntegratedDecisionTreeModel, EdgeIntegratedDecisionTreeModel, MomentIntegratedDecisionTreeModel}
import jb.parser.TreeParser
import jb.prediction.Predictions.predictBaseClfs
import jb.selector.FeatureSelectors
import jb.server.SparkEmbedded
import jb.tester.FullTester.{testI, testMv, testRF}
import jb.util.Const._
import jb.util.Util._
import jb.util.functions.DistMappingFunctions._
import jb.util.functions.WeightAggregators.sumOfWeights
import jb.vectorizer.FeatureVectorizers.getFeatureVectorizer
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}

class Runner(val nClassif: Int, var nFeatures: Int, val coefficients: Coefficients, val mappingModel: MappingModel) {

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

    val (mins, maxes) = getExtrema(input, getSelectedFeatures(dataPrepModel))

    val nSubsets = nClassif + 2
    val subsets = input.randomSplit(IntStream.range(0, nSubsets).mapToDouble(_ => 1D / nSubsets).toArray)
    recacheInput2Subsets(input, subsets)
    val (trainingSubsets, cvSubset, testSubset) = dispenseSubsets(subsets)
    val trainingSubset = unionSubsets(trainingSubsets)

    val dt = new DecisionTreeClassifier()
      .setLabelCol(LABEL)
      .setFeaturesCol(FEATURES)
      .setImpurity(Config.impurity)
      .setMaxDepth(Config.maxDepth)
    val baseModels = trainingSubsets.map(subset => dt.fit(subset))

    val testedSubset = predictBaseClfs(baseModels, testSubset)
    val mvQualityMeasure = testMv(testedSubset, nClassif)
    val rfQualityMeasure = testRF(trainingSubset, testSubset, nClassif)

    val edges: Array[Array[Edge]] = if (coefficients.edgeDependent) getEdges(mins, maxes, baseModels) else null
    // TODO: moments - bottleneck
    // TODO: batch by moments calculation method
    val moments = if (coefficients.momentDependent) mappingModel match {
      case PreTraining() => calculateMomentsByLabels(input, getSelectedFeatures(dataPrepModel))
      case PostTrainingCV() => calculateMomentsByPredictionCollectively(cvSubset, getSelectedFeatures(dataPrepModel), baseModels, false)
      case PostTrainingTrain() => calculateMomentsByPredictionRespectively(trainingSubsets, getSelectedFeatures(dataPrepModel), baseModels, false)
      case PostTrainingAll() => calculateMomentsByPredictionCollectively(input, getSelectedFeatures(dataPrepModel), baseModels, false)
      case PostTrainingCVFiltered() => calculateMomentsByPredictionCollectively(cvSubset, getSelectedFeatures(dataPrepModel), baseModels, true)
      case PostTrainingTrainFiltered() => calculateMomentsByPredictionRespectively(trainingSubsets, getSelectedFeatures(dataPrepModel), baseModels, true)
      case PostTrainingAllFiltered() => calculateMomentsByPredictionCollectively(input, getSelectedFeatures(dataPrepModel), baseModels, true)
    } else null
    val integratedModel =
      if (coefficients.onlyEdgeDependent) new EdgeIntegratedDecisionTreeModel(baseModels, singleMapping(coefficients), sumOfWeights, edges)
      else if (coefficients.onlyMomentDependent) new MomentIntegratedDecisionTreeModel(baseModels, singleMapping(coefficients), sumOfWeights, moments)
      else new CombinedIntegratedDecisionTreeModel(baseModels, composedMapping(coefficients), sumOfWeights, edges, moments)
    val iPredictions = integratedModel.transform(testedSubset)
    val iQualityMeasure = testI(iPredictions, testedSubset)

    clearCache(subsets)

    Array(mvQualityMeasure._1, if (mvQualityMeasure._2.isNaN) 0D else mvQualityMeasure._2, mvQualityMeasure._3, mvQualityMeasure._4,
      rfQualityMeasure._1, if (rfQualityMeasure._2.isNaN) 0D else rfQualityMeasure._2, rfQualityMeasure._3, rfQualityMeasure._4,
      iQualityMeasure._1, if (iQualityMeasure._2.isNaN) 0D else iQualityMeasure._2, iQualityMeasure._3, iQualityMeasure._4)

  }

  private def getEdges(mins: Array[Double], maxes: Array[Double], baseModels: Array[DecisionTreeClassificationModel]): Array[Array[Edge]] = {
    val rootRect = Rect(mins, maxes)
    val treeParser = new TreeParser()
    val rects = baseModels.map(model => treeParser.dt2rect(rootRect, model.rootNode))
    rects.map(treeParser.rects2edges)
  }

}
