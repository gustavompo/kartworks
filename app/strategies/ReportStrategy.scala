package strategies

import play.api.libs.json.{ JsValue, Json }

import controllers.LapEvent

trait ReportStrategy {
  def name: String

  protected def innerCreateReport(cumulativeEvents: List[LapEvent]): JsValue

  def createReport(cumulativeEvents: List[LapEvent]) = {
    Json.obj(
      "reportName" -> name,
      "content" -> innerCreateReport(cumulativeEvents)
    )
  }

}
