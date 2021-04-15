package jb.conf

import jb.util.functions.{MetricFunctions, WeightingFunctions}

object Config {

  /** Models */
  val maxDepth: Int = 10
  val impurity = "gini"
  val maxBins = 64

  /** Parametrizing */
  val numberOfClassifiers: Array[Int] = Array(3, 5, 7)
  val metricFunction: (List[Double], List[Double]) => Double = MetricFunctions.euclidean
  val mappingFunction: Map[Double, Map[Double, Int]] => Double = WeightingFunctions.halfByDist

  /** Result catcher */
  val batch: Int = 10 // minimal number of results
  val treshold: Double = .4
  val minIter: Int = 10
  val maxIter: Int = 200

  /** Other */
  val recalculate = false
  val fScoreBeta = 1

}
