package jb.util.functions

import jb.model.CountingCube

object MetricFunctions {

  val constant: (CountingCube, CountingCube) => Double = (cube, neighbor) => 0 // TODO: refactor constant functions to typeclasses & implicits

}
