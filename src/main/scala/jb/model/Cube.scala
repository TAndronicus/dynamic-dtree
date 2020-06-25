package jb.model

case class Cube(min: Array[Double], max: Array[Double])

case class CountingCube(min: Array[Double], max: Array[Double], labelCount: Map[Int, Int])

case class LabelledCube(min: Array[Double], max: Array[Double], label: Int)
