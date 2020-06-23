package jb.model.dt

import jb.model.Edge
import org.scalatest.FunSuite

class CombinedIntegratedDecisionTreeModelTest extends FunSuite {

  test("distance from point to edge") {
    val model = new CombinedIntegratedDecisionTreeModel(null, null, null, null, null)
    val point = Array(1D, 1)
    val edge0 = Edge(Array(0, 0), Array(2, 0))
    val edge1 = Edge(Array(3, 0), Array(3, 5))
    val edge2 = Edge(Array(point(0) + 3, point(1) + 4), Array(point(0) + 3, point(1) + 6))

    assert(model.distUnsigned(edge0, point) == 1)
    assert(model.distUnsigned(edge1, point) == 2)
    assert(model.distUnsigned(edge2, point) == 5)
  }

  test("min distance from point to edge") {
    val point = Array(1D, 1)
    val edge0 = Edge(Array(0, 0), Array(2, 0))
    val edge1 = Edge(Array(3, 0), Array(3, 5))
    val edge2 = Edge(Array(point(0) + 3, point(1) + 4), Array(point(0) + 3, point(1) + 6))
    val model = new CombinedIntegratedDecisionTreeModel(null, null, null, Array(Array(edge0, edge1, edge2)), null)
    assert(model.minDistUnsigned(0, point) == 1)
  }

}
