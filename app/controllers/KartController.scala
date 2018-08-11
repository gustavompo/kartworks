package controllers

import scala.concurrent.ExecutionContext

import play.api.libs.json.Json
import play.api.mvc._

import controllers.validation.KartInputValidation
import javax.inject.{ Inject, Singleton }
import strategies.{ DefaultReportStrategy, LapEventAccumulator, LastLapFinder, ReportStrategy }


@Singleton
class KartController @Inject()(
    validation: KartInputValidation,
    parse: PlayBodyParsers
)(implicit ec: ExecutionContext) extends InjectedController {

  val strategies = Map[String, ReportStrategy](
    "default" -> new DefaultReportStrategy(new LastLapFinder(new LapEventAccumulator))
  )
  private val parseLapEvents = parse.using { _ =>
    parse.tolerantText.validate { x =>
      validation
          .parseValidate(x)
          .left.map(BadRequest(_))
    }
  }

  def report(strategy: String) = Action(parseLapEvents) { req =>
    defineValidateStrategies(strategy) match {
      case Left(err) => NotFound(err)
      case Right(strats) =>
        val res = Json.obj("reports" -> strats.map(_.createReport(req.body)))
        Ok(res)
    }

  }

  private def defineValidateStrategies(strs: String) = {
    val found = strs.split(",").map(st => (st, strategies.get(st)))
    val invalid = found.exists { case (_, out) => out.isEmpty }
    if (found.isEmpty || invalid)
      Left(s"Invalid strategy [$strs]. The options are (you can use more than one, separated by comma) [${strategies.keys.mkString(",")}]")
    else
      Right(found.flatMap { case (_, st) => st })
  }
}


case class LapEvent(
    time: String,
    carNumber: String,
    pilotName: String,
    lap: Int,
    lapTime: String,
    lapSpeed: Double
)