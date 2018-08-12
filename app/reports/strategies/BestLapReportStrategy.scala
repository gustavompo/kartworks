package reports.strategies

import play.api.libs.json.{ JsArray, JsValue, Json }

import com.google.inject.multibindings.ProvidesIntoSet

import models.LapLogEntry

@ProvidesIntoSet
class BestLapReportStrategy extends ReportStrategy {

  def name = "best-lap"

  override def innerCreateReport(laps: List[LapLogEntry]): JsValue = {
    if (laps.isEmpty) JsArray(List())
    else {
      val lap = laps.maxBy(_.lapSpeed)
      Json.obj(
        "name" -> lap.pilotName,
        "lap" -> lap.lap,
        "time" -> lap.lapTime.toString
      )
    }
  }
}

