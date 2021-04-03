package jb.util.functions

object MetricFunctions {

  val constant: (List[Double], List[Double]) => Double = (_, _) => 0

  val euclidean: (List[Double], List[Double]) => Double = (cube, neighbor) => math.sqrt(
    cube.zip(neighbor)
      .map { case (cubeMid, nMid) => math.pow(cubeMid - nMid, 2) }
      .sum
  )

  val euclideanSquared: (List[Double], List[Double]) => Double = (cube, neighbor) => cube.zip(neighbor)
    .map { case (cubeMid, nMid) => math.pow(cubeMid - nMid, 2) }
    .sum

  val euclideanMod: Int => (List[Double], List[Double]) => Double = pow => (cube, neighbor) => cube.zip(neighbor)
    .map { case (cubeMid, nMid) => math.pow(cubeMid - nMid, pow) }
    .sum

  val manhattan: (List[Double], List[Double]) => Double = (cube, neighbor) => cube.zip(neighbor)
    .map { case (cubeMid, nMid) => math.abs(cubeMid - nMid) }
    .sum

  val chebyschev: (List[Double], List[Double]) => Double = (cube, neighbor) => cube.zip(neighbor)
    .map { case (cubeMid, nMid) => math.abs(cubeMid - nMid) }
    .max

}
