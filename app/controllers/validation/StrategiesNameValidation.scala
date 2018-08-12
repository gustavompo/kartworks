package controllers.validation

import reports.strategies.ReportStrategy

class StrategiesNameValidation {
  def defineValidateStrategies(inputStrategies: String)(implicit strategies: Map[String, ReportStrategy]) = {
    val found = inputStrategies.split(",").map(st => (st, strategies.get(st)))
    val invalid = found.exists { case (_, out) => out.isEmpty }
    if (found.isEmpty || invalid)
      Left(s"Invalid strategy [$inputStrategies]. The options are (you can use more than one, separated by comma) [${strategies.keys
          .mkString(",")}]")
    else
      Right(found.flatMap { case (_, st) => st })
  }
}
