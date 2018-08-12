package controllers.validation

import reports.strategies.ReportStrategy

class StrategyNameValidation {
  def defineValidateStrategies(inputStrategies: String)(implicit strategies: Map[String, ReportStrategy]) = {
    val mappedStrategies = inputStrategies
        .split(",")
        .map(strategies.get)
        .toList

    if (mappedStrategies.exists(_.isEmpty))
      Left(s"Invalid strategy [$inputStrategies]. The options are (you can use more than one, separated by comma) [${strategies.keys.mkString(",")}]")
    else
      Right(mappedStrategies.flatten)
  }
}
