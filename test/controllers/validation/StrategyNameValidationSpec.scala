package controllers.validation

import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec

import reports.strategies.{ BestLapReportStrategy, DefaultReportStrategy, ReportStrategy }
import reports.transforms.{ LapEventAccumulator, LastLapFinder }


class StrategyNameValidationSpec extends PlaySpec with MustMatchers {

  val target = new StrategyNameValidation
  val allStrategies = List(
    new DefaultReportStrategy(new LastLapFinder(new LapEventAccumulator)),
    new BestLapReportStrategy
  ).map(s => (s.name, s)).toMap

  "StrategyName validation" must {

    "Left(error) given no implicit strategy" in {
      implicit val strategies = Map[String, ReportStrategy]()
      target.defineValidateStrategies("default") must matchPattern {
        case Left(err: String) if err.contains("The options are (you can use more than one, separated by comma) []") =>
      }
    }

    "Left(error) given strategy not found" in {
      implicit val strategies = allStrategies
      target.defineValidateStrategies("high-ground-strategy") must matchPattern {
        case Left(err: String) if err.contains("Invalid strategy [high-ground-strategy]") =>
      }
    }

    "Right(strategies) given a single valid strategy name" in {
      implicit val strategies = allStrategies
      target.defineValidateStrategies(allStrategies.values.head.name) must matchPattern {
        case Right(strategies: List[ReportStrategy]) if strategies.head.name == allStrategies.values.head.name =>
      }
    }

    "Right(strategies) given multiple valid strategy names" in {
      implicit val strategies = allStrategies
      target.defineValidateStrategies(allStrategies.values.map(_.name).mkString(",")) must matchPattern {
        case Right(strategies: List[ReportStrategy]) if strategies.size == allStrategies.size =>
      }
    }

  }

}
