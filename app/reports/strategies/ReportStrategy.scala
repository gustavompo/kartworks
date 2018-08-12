package reports.strategies

import play.api.libs.json.{ JsValue, Json }

import models.LapLogEntry

trait ReportStrategy {
  def name: String

  protected def innerCreateReport(cumulativeEvents: List[LapLogEntry]): JsValue

  def createReport(cumulativeEvents: List[LapLogEntry]) = {
    Json.obj(
      "reportName" -> name,
      "content" -> innerCreateReport(cumulativeEvents)
    )
  }

}
