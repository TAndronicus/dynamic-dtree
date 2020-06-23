package jb.model.dt

import jb.model.Edge

object IntegratedDecisionTreeUtil {

  def pointDist(p1: Array[Double], p2: Array[Double]): Double = {
    math.sqrt(p1.indices.map(i => math.pow(p1(i) - p2(i), 2)).sum)
  }

  def edgeOvelaps(edge: Edge, point: Array[Double], dim: Int): Boolean = {
    edge.min(dim) <= point(dim) && edge.max(dim) >= point(dim) && edge.min(dim) != edge.max(dim)
  }

}
