package jb

import jb.server.SparkEmbedded

object ExperimentPlan {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogError()
    val nClassifs = Array(3, 5, 7, 9)
    val nFeatures = 2
    val alphas = Array(0, .3, .7, 1)
    val betas = Array(0, .5)
    val gammas = Array(5, 20)
    //    val (beta1, beta2) = (.5, 0)
    //    val (gamma1, gamma2) = (20, 5)

    for (nC <- nClassifs) {
      MultiRunner.run(nC, nFeatures)
    }
  }

}
