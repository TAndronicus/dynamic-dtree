package jb.model.dt

import jb.model.dt.IntegratedDecisionTreeUtil.pointDist
import jb.util.Const.FEATURES
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.linalg.{DenseVector, SparseVector}
import org.apache.spark.sql.DataFrame

class MomentIntegratedDecisionTreeModel(
                                         val baseModels: Array[DecisionTreeClassificationModel],
                                         val distMappingFunction: Double => Double,
                                         val weightAggregator: IndexedSeq[(Double, Double)] => Double,
                                         val moments: Map[Double, Array[Double]]
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
    baseModels.indices.map(i => {
      val label = baseModels(i).predict(new DenseVector(obj))
      (label, weightedDist(label, obj))
    })
      .groupBy(_._1)
      .mapValues(weightAggregator)
      .reduce((l1, l2) => if (l1._2 > l2._2) l1 else l2)._1
  }

  def weightedDist(label: Double, obj: Array[Double]): Double = {
    distMappingFunction(pointDist(moments(label), obj))
  }

}
