package jb.model

case class Cube(min: List[Double], max: List[Double])

case class CountingCube(min: List[Double], max: List[Double], labelCount: Map[Int, Int])

case class LabelledCube(min: List[Double], max: List[Double], label: Int)
