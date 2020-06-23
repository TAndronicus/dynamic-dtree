package jb.model.dt

import jb.model.Edge
import jb.util.Const.FEATURES
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.linalg.{DenseVector, SparseVector}
import org.apache.spark.sql.DataFrame

class EdgeIntegratedDecisionTreeModel(
                                       val baseModels: Array[DecisionTreeClassificationModel],
                                       val distMappingFunction: Double => Double,
                                       val weightAggregator: IndexedSeq[(Double, Double)] => Double,
                                       val edges: Array[Array[Edge]] // edges pro base model (edges.size == baseModels.size)
                                     )
  extends IntegratedDecisionTreeModel {

  override def transform(dataframe: DataFrame): Array[Double] = {
    dataframe.select(FEATURES).collect().map({ row =>
      row.toSeq.head match {
        case dense: DenseVector =>
          transform(dense.toArray)
        case sparse: SparseVector =>
          transform(sparse.toArray)
      }
    })
  }

  def transform(obj: Array[Double]): Double = {
    edges.indices.map(i => (baseModels(i).predict(new DenseVector(obj)), weightedDist(edges(i), obj))) // (label, weight)
      .groupBy(_._1)
      .mapValues(weightAggregator)
      .reduce((l1, l2) => if (l1._2 > l2._2) l1 else l2)._1
  }

  def weightedDist(edgeModel: Array[Edge], obj: Array[Double]): Double = {
    distMappingFunction(minDistUnsigned(edgeModel, obj))
  }

  def minDistUnsigned(edgeModel: Array[Edge], obj: Array[Double]): Double = {
    if (edgeModel.isEmpty) {
      return .5 // Data is normalized
    }
    edgeModel.map(edge => distUnsigned(edge, obj)).min
  }

  def distUnsigned(edge: Edge, obj: Array[Double]): Double = {
    if (edgeOvelaps(edge, obj, 0)) {
      math.abs(edge.min(1) - obj(1))
    } else if (edgeOvelaps(edge, obj, 1)) {
      math.abs(edge.min(0) - obj(0))
    } else {
      math.min(pointDist(edge.min, obj), pointDist(edge.max, obj))
    }
  }

  def pointDist(p1: Array[Double], p2: Array[Double]): Double = {
    math.sqrt(p1.indices.map(i => math.pow(p1(i) - p2(i), 2)).sum)
  }

  private def edgeOvelaps(edge: Edge, obj: Array[Double], dim: Int): Boolean = {
    edge.min(dim) <= obj(dim) && edge.max(dim) >= obj(dim) && edge.min(dim) != edge.max(dim)
  }

}
