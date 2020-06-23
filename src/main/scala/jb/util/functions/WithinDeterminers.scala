package jb.util.functions

object WithinDeterminers {

  def spansMid(elementaryCubeMins: Array[Double], elementaryCubeMaxes: Array[Double]): (Array[Double], Array[Double]) => Boolean = {
    (treeCubeMins: Array[Double], treeCubeMaxes: Array[Double]) =>
      elementaryCubeMins.indices
        .map(i => (elementaryCubeMins(i) + elementaryCubeMaxes(i)) / 2D >= treeCubeMins(i) && (elementaryCubeMins(i) + elementaryCubeMaxes(i)) / 2D <= treeCubeMaxes(i))
        .reduce((b1, b2) => b1 && b2)
  }

}
