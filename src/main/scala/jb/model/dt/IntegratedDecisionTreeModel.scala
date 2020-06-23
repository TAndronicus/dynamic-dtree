package jb.model.dt

import org.apache.spark.sql.DataFrame

abstract class IntegratedDecisionTreeModel {

  def transform(dataframe: DataFrame): Array[Double]
}
