package reports.strategies

import play.api.libs.json.{ JsArray, JsValue, Json }

import com.google.inject.multibindings.ProvidesIntoSet

import models.LapLogEntry

@ProvidesIntoSet
class BestPilotLapReportStrategy extends ReportStrategy {

  def name = "best-pilot-lap"

  override def innerCreateReport(laps: List[LapLogEntry]): JsValue = {
    val bestLaps = laps
        .groupBy(_.carNumber)
        .map { case (_, pilotLaps) =>
          pilotLaps.maxBy(_.lapSpeed)
        }.toList.sortBy(_.lapSpeed).reverse

    val reportItems = bestLaps.map { lap =>
      Json.obj(
        "pilotNumber" -> lap.carNumber,
        "name" -> lap.pilotName,
        "bestLap" -> lap.lap,
        "totalTime" -> lap.lapTime.toString
      )
    }
    JsArray(reportItems)
  }
}

