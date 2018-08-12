package controllers

import scala.concurrent.ExecutionContext

import play.api.libs.json.Json
import play.api.mvc._

import controllers.validation.{ LapLogEntryValidation, StrategiesNameValidation }
import javax.inject.{ Inject, Singleton }
import reports.strategies._
import reports.transforms.LastLapFinder
import scala.collection.JavaConverters._


@Singleton
class KartReportController @Inject()(
    validation: LapLogEntryValidation,
    parse: PlayBodyParsers,
    lastLapFinder: LastLapFinder,
    strategiesValidation: StrategiesNameValidation,
    rawStrategies: java.util.Set[ReportStrategy]
)(implicit ec: ExecutionContext) extends InjectedController {

  implicit val strategies = rawStrategies.asScala.map(s => (s.name, s)).toMap

  def report(strategy: String) = Action(parseLapLogEntries) { req =>
    strategiesValidation.defineValidateStrategies(strategy) match {
      case Left(err) =>
        NotFound(err)

      case Right(validStrategies) =>
        val res = Json.obj("reports" -> validStrategies.map(_.createReport(req.body)))
        Ok(res)
    }
  }

  private val parseLapLogEntries = parse.using { _ =>
    parse.tolerantText.validate { x =>
      validation
          .parseValidate(x)
          .left.map(BadRequest(_))
    }
  }
}


