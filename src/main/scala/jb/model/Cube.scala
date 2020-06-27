package jb.model

import org.apache.spark.ml.linalg

case class Cube(min: List[Double], max: List[Double]) {
  def getMidAsMlVector = new linalg.DenseVector(min.zip(max)
    .map { case (xMin, xMax) => (xMin + xMax) / 2 }
    .toArray)

}

case class CountingCube(min: List[Double], max: List[Double], labelCount: Map[Double, Int])

object CountingCube {
  def fromCube(cube: Cube, labelCount: Map[Double, Int]) = CountingCube(cube.min, cube.max, labelCount)
}

case class LabelledCube(min: List[Double], max: List[Double], label: Double)
