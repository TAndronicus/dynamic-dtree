package jb.util.functions

import jb.model.Coefficients

object DistMappingFunctions {

  val simpleMapping: Double => Double = (dist: Double) => math.exp(exponentialCoefficient(dist))
  val momentMappingFunction: (Double, Double) => Double = (distFromBorder: Double, distFromMoment: Double) =>.7 * simpleMapping(distFromBorder) +.3 * simpleMapping(distFromMoment)
  val parametrizedMomentMappingFunction: Double => (Double, Double) => Double = (alpha: Double) => (distFromBorder: Double, distFromMoment: Double) => alpha * simpleMapping(distFromBorder) + (1 - alpha) * simpleMapping(distFromMoment)

  val baseSingleMapping: (Double, Double, Double) => Double = (dist, beta, gamma) => math.exp(-gamma * math.pow(dist - beta, 2))
  val baseComposedMapping: (Double, Double, Coefficients) => Double =
    (edgeDist, momentDist, coefficients) => coefficients.alpha * baseSingleMapping(edgeDist, coefficients.beta1, coefficients.gamma1) +
      (1 - coefficients.alpha) * baseSingleMapping(momentDist, coefficients.beta2, coefficients.gamma2)

  val singleMapping: Coefficients => Double => Double = coefficients => dist => baseSingleMapping(dist, coefficients.getBeta, coefficients.getGamma)
  val composedMapping: Coefficients => (Double, Double) => Double = coefficients => (edgeDist, momentDist) => baseComposedMapping(edgeDist, momentDist, coefficients)

  private def exponentialCoefficient(dist: Double): Double = {
    math.pow(10 * dist - 3, 2) / -4
  }

}
