package jb.parser

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
    val (x1cutpoints, x2cutpoints) = trees.map(_.rootNode)
      .flatMap(extractCutpointsRecursively)
      .distinct
      .partition({ case (feature, _) => feature == 0 })
    print("")
  }

}
