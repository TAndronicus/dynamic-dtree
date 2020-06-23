package jb.parser

import jb.model._
import org.apache.spark.ml.tree.{ContinuousSplit, InternalNode, Node}

class TreeParser() {

  def dt2rect(parent: Rect, node: Node): Array[Rect] = {
    node match {
      case _: InternalNode =>
        val interNode = node.asInstanceOf[InternalNode]

        val newMax = parent.max.clone()
        newMax(interNode.split.featureIndex) = interNode.split.asInstanceOf[ContinuousSplit].threshold
        val newMin = parent.min.clone()
        newMin(interNode.split.featureIndex) = interNode.split.asInstanceOf[ContinuousSplit].threshold

        val leftChild = parent.copy(max = newMax)
        val rightChild = parent.copy(min = newMin)

        dt2rect(leftChild, node.asInstanceOf[InternalNode].leftChild) ++ dt2rect(rightChild, node.asInstanceOf[InternalNode].rightChild)
      case _ =>
        Array(parent.copy(label = node.prediction))
    }
  }

  def rects2edges(rects: Array[Rect]): Array[Edge] = {
    val indices = for (i <- rects.indices; j <- rects.indices if i < j) yield (i, j)
    indices.map(tuple => (rects(tuple._1), rects(tuple._2))).filterNot(areOfSameClasses).filter(areAdjacent).map(createEdge).toArray
  }

  def areAdjacent(tuple: (Rect, Rect)): Boolean = {
    val (r1, r2) = tuple
    for (dim <- r1.min.indices) {
      if (math.max(r1.min(dim), r2.min(dim)) > math.min(r1.max(dim), r2.max(dim))) return false
    }
    true
  }

  def areOfSameClasses(tuple: (Rect, Rect)): Boolean = {
    tuple._1.label == tuple._2.label
  }

  def createEdge(tuple: (Rect, Rect)): Edge = {
    val (r1, r2) = tuple
    val (mins, maxes) = (new Array[Double](r1.min.length), new Array[Double](r1.min.length))
    for (dim <- r1.min.indices) {
      mins(dim) = math.max(r1.min(dim), r2.min(dim))
      maxes(dim) = math.min(r1.max(dim), r2.max(dim))
    }
    Edge(mins, maxes)
  }

}
