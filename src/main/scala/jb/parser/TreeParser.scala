package jb.parser

import jb.model.Cube
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.tree.{ContinuousSplit, InternalNode, LeafNode, Node}

class TreeParser {

  def extractCutpointsRecursively(tree: Node): List[Tuple2[Int, Double]] = {
    tree match {
      case leaf: LeafNode => List()
      case branch: InternalNode => branch.split match {
        case contSplit: ContinuousSplit => {
          (extractCutpointsRecursively(branch.leftChild)
            ::: ((contSplit.featureIndex -> contSplit.threshold)
            :: extractCutpointsRecursively(branch.rightChild)))
        }
        case _ => throw Exception("Unsupported split")
      }
      case _ => throw Exception("Unsupported node")
    }
  }

  def extractCutpoints(trees: List[DecisionTreeClassificationModel]): Unit = {
    trees.flatMap(extractCutpointsTree)
  }

  private def extractCutpointsTree(tree: DecisionTreeClassificationModel): List[Cube] = {
    extractCutpointsRecursively(tree.rootNode, Array())
  }

}
