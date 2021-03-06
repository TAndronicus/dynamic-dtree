package jb.util.result

import jb.conf.Config
import jb.model.Measurements

import java.io.{File, PrintWriter}


class EagerResultCatcher(val treshold: Double, val batchSize: Int, val maxIter: Int) extends ResultCatcher {

  val container = new Array[Array[Array[Double]]](batchSize)
  var containerIndex = 0
  var currentIndex = 0

  def consume(scores: Array[Array[Double]]): Unit = {
    if (isValid(scores)) {
      container(containerIndex) = scores
      containerIndex += 1
      println("Accepted")
    } else {
      println("Rejected")
    }
    currentIndex += 1
  }

  override def isValid(scores: Array[Array[Double]]): Boolean =
    Measurements.integratedQuality(scores) >= Config.treshold

  def isFull: Boolean = {
    currentIndex >= maxIter || maxIter - currentIndex - 1 < batchSize - containerIndex || isFilled
  }

  def aggregate: Array[Array[Double]] = {
    if (isFilled) {
      val result = new Array[Array[Double]](container(0).length)
      for (midIndex <- container(0).indices) {
        val partialResult = new Array[Double](container(0)(0).length)
        for (innerIndex <- container(0)(0).indices) {
          partialResult(innerIndex) = container.map(score => score(midIndex)(innerIndex)).sum / container.length
        }
        result(midIndex) = partialResult
      }
      println(s"Consumed: ${container.length}/$currentIndex")
      result
    } else {
      throw new RuntimeException("Not filled")
    }
  }

  def isFilled: Boolean = {
    containerIndex >= batchSize
  }

  def writeScores(finalScores: Array[Array[Double]], args: Array[String]): Unit = {
    val pw = new PrintWriter(new File("result"))
    finalScores.foreach(scores => pw.println(scores.map(_.toString).reduce((s1, s2) => s1 + "," + s2)))
    pw.flush()
    pw.close()
  }

}
