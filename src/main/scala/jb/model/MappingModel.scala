package jb.model

sealed abstract class MappingModel

case class PreTraining() extends MappingModel // All objects by given labels
case class PostTrainingCV() extends MappingModel // CV objects by all predictions
case class PostTrainingTrain() extends MappingModel // Training objects by all predictions
case class PostTrainingAll() extends MappingModel // All objects by all predictions
case class PostTrainingCVFiltered() extends MappingModel // CV objects by filtered predictions
case class PostTrainingTrainFiltered() extends MappingModel // Training objects by filtered predictions
case class PostTrainingAllFiltered() extends MappingModel // All objects by filtered predictions
