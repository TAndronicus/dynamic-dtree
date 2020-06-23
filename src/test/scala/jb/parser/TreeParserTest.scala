package jb.parser

import jb.model.{Edge, Rect}
import org.scalatest.FunSuite

class TreeParserTest extends FunSuite {

  test("rects2edges") {
    // given
    val rects = Array(
      Rect(Array(0, 0), Array(3, 2)),
      Rect(Array(0, 2), Array(3, 3), 1),
      Rect(Array(3, 0), Array(5, 1), 1),
      Rect(Array(3, 1), Array(5, 3))
    )
    val expectedEdges = Array(
      Edge(Array(0, 2), Array(3, 2)),
      Edge(Array(3, 2), Array(3, 3)),
      Edge(Array(3, 1), Array(5, 1)),
      Edge(Array(3, 0), Array(3, 1))
    )

    // when
    val treeParser = new TreeParser()
    val edges = treeParser.rects2edges(rects)

    // then
    expectedEdges.foreach(edge => assert(edges.contains(edge)))
  }

  test("areAdjacent positive") {
    // given
    val r1 = Rect(Array(1, 2, 3), Array(2, 4, 6))
    val r2 = Rect(Array(1, 2, 6), Array(7, 8, 9))

    // when
    val treeParser = new TreeParser()
    val areAdjacent = treeParser.areAdjacent((r1, r2))

    // then
    assert(areAdjacent)
  }

  test("areAdjacent negative 1") {
    // given
    val r1 = Rect(Array(0, 0, 0), Array(1, 2, 3))
    val r2 = Rect(Array(2, 4, 6), Array(3, 6, 9))

    // when
    val treeParser = new TreeParser()
    val areAdjacent = treeParser.areAdjacent((r1, r2))

    // then
    assert(!areAdjacent)
  }

  test("areAdjacent negative 2") {
    // given
    val r1 = Rect(Array(0, 2), Array(3, 3))
    val r2 = Rect(Array(3, 0), Array(5, 1))

    // when
    val treeParser = new TreeParser()
    val areAdjacent = treeParser.areAdjacent((r1, r2))

    // then
    assert(!areAdjacent)
  }

  test("areOfSameLabel") {
    // given
    val r1 = Rect(Array(1, 2, 3), Array(2, 4, 6))
    val r2 = Rect(Array(1, 2, 3), Array(2, 4, 6))
    val r3 = Rect(Array(1, 2, 3), Array(2, 4, 6), 1D)

    // when
    val treeParser = new TreeParser()
    val same12 = treeParser.areOfSameClasses((r1, r2))
    val same13 = treeParser.areOfSameClasses((r1, r3))

    // then
    assert(same12)
    assert(!same13)
  }

  test("createEdge") {
    // given
    val r1 = Rect(Array(0, 0), Array(5, 3))
    val r2 = Rect(Array(5, 1), Array(6, 2))

    // when
    val treeParser = new TreeParser()
    val edge = treeParser.createEdge((r1, r2))

    // then
    assert(edge.min(0) == edge.max(0))
    assert(edge.min(1) == 1)
    assert(edge.max(1) == 2)
  }

}
