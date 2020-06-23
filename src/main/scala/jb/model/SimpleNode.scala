package jb.model

abstract class SimpleNode

case class InternalSimpleNode(leftChild: SimpleNode, rightChild: SimpleNode, split: SimpleSplit) extends SimpleNode

case class LeafSimpleNode(label: Double) extends SimpleNode
