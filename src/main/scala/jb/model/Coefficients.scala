package jb.model

class Coefficients(
                    val alpha: Double,
                    val beta1: Double, // edge
                    val beta2: Double, // moment
                    val gamma1: Double, // edge
                    val gamma2: Double // moment
                  ) {

  def validate(): Unit = {
    require(alpha >= 0)
    require(alpha <= 1)
    require(beta1 >= 0)
    require(beta1 <= 1)
    require(beta2 >= 0)
    require(beta2 <= 1)
    require(gamma1 >= 1)
    require(gamma2 >= 1)
  }

  def getBeta: Double = if (onlyEdgeDependent) beta1 else if (onlyMomentDependent) beta2 else throw new RuntimeException("Ambiguous coefficient")

  def getGamma: Double = if (onlyEdgeDependent) gamma1 else if (onlyMomentDependent) gamma2 else throw new RuntimeException("Ambiguous coefficient")

  def edgeDependent: Boolean = alpha != 0

  def onlyEdgeDependent: Boolean = alpha == 1

  def momentDependent: Boolean = alpha != 1

  def onlyMomentDependent: Boolean = alpha == 0

  def getAllCoefficients = Array(alpha, beta1, beta2, gamma1, gamma2)

  override def toString: String = s"alpha: ${alpha}, beta1: ${beta1}, beta2: ${beta2}, gamma1: ${gamma1}, gamma2: ${gamma2}"

  override def equals(obj: Any): Boolean =
    obj.isInstanceOf[Coefficients] &&
      this.alpha == obj.asInstanceOf[Coefficients].alpha &&
      this.beta1 == obj.asInstanceOf[Coefficients].beta1 &&
      this.beta2 == obj.asInstanceOf[Coefficients].beta2 &&
      this.gamma1 == obj.asInstanceOf[Coefficients].gamma1 &&
      this.gamma2 == obj.asInstanceOf[Coefficients].gamma2

}

object Coefficients {

  def apply(
             alpha: Double,
             beta1: Double,
             beta2: Double,
             gamma1: Double,
             gamma2: Double
           ): Coefficients = new Coefficients(
    alpha,
    beta1,
    beta2,
    gamma1,
    gamma2
  )

  def fromArray(coefficients: Double*) = {
    require(coefficients.length == 5)
    new Coefficients(coefficients(0), coefficients(1), coefficients(2), coefficients(3), coefficients(4))
  }

}
