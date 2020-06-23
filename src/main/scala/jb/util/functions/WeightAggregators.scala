package jb.util.functions

object WeightAggregators {

  val sumOfWeights: IndexedSeq[(Double, Double)] => Double = ar => ar.map(_._2).sum

}
