package jb.util

import jb.server.SparkEmbedded
import org.scalatest.FunSuite

class UtilTest extends FunSuite {

  test("should calculate right moments") {
    val df = SparkEmbedded.ss.createDataFrame(Seq(
      (0, 2, 5, 6, 7),
      (0, 4, 7, 5, 8),
      (0, 6, 9, 6, 9),
      (0, 7, 9, 7, 7),
      (1, 1, 2, 3, 4),
      (1, 2, 4, 6, 8)
    )).toDF("label", "_c0", "_c2", "_c3", "_c1")
    val selectedF = Array(0, 3, 2)
    val map = Util.calculateMomentsByLabels(df, selectedF)
    assert(map(0D).deep == Array(4.75, 6, 7.5).deep)
    assert(map(1D).deep == Array(1.5, 4.5, 3).deep)
  }

}
