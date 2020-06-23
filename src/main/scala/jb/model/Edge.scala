package jb.model

case class Edge(var min: Array[Double], var max: Array[Double]) {

  override def hashCode(): Int = (13 * min.sum + 17 * max.sum).toInt

  override def equals(obj: Any): Boolean = {
    obj match {
      case edge: Edge =>
        val (oMin, oMax) = (edge.min, edge.max)
        if (oMin.length == min.length && oMax.length == max.length) {
          for (index <- oMin.indices) {
            if (oMin(index) != min(index) || oMax(index) != max(index)) {
              return false
            }
          }
          return true
        }
        false
      case _ => false
    }
  }

}
