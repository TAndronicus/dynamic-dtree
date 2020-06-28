package jb.util.functions

object WeightingFunctions {

    val linear: Map[Double, Map[Double, Int]] => Double = _
      .map { case (dist, labels) => labels.mapValues(_ * (1 - dist)) }
      .reduce((m1, m2) => (m1.toSeq ++ m2.toSeq)
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)) // TODO: compose with type classes: https://stackoverflow.com/questions/20047080/scala-merge-map
      .maxBy { case (_, weight) => weight }
      ._1

}
