package jb.model.dt

import jb.model.Edge
import org.scalatest.FunSuite

class IntegratedDecisionTreeUtilTest extends FunSuite {

  test("point dist") {
    val (x, y) = (2, 3.5)
    assert(IntegratedDecisionTreeUtil.pointDist(Array(x, y), Array(x + 3, y + 4)) == 5)
  }

  test("edge overlapping") {
    val model = new CombinedIntegratedDecisionTreeModel(null, null, null, null, null)
    val edge0 = Edge(Array(0, 0), Array(2, 0))
    val edge1 = Edge(Array(0, 1), Array(0, 2))
    val point = Array(1D, 0)

    assert(IntegratedDecisionTreeUtil.edgeOvelaps(edge0, point, 0))
    assert(!IntegratedDecisionTreeUtil.edgeOvelaps(edge0, point, 1))
    assert(!IntegratedDecisionTreeUtil.edgeOvelaps(edge1, point, 0))
    assert(!IntegratedDecisionTreeUtil.edgeOvelaps(edge1, point, 1))
  }

}
