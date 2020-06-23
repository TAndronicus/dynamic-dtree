package jb.model

import org.scalatest.FunSuite

class EdgeTest extends FunSuite {

  test("equals") {
    assert(Edge(Array(1D), Array(2D)).equals(Edge(Array(1D), Array(2D))))
  }

}
