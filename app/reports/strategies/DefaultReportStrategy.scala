package reports.strategies

import play.api.libs.json.{ JsArray, JsValue, Json }

import com.google.inject.multibindings.ProvidesIntoSet

import javax.inject.Inject
import models.LapLogEntry
import reports.transforms.LastLapFinder

@ProvidesIntoSet
class DefaultReportStrategy @Inject()(
    lastLapFinder: LastLapFinder
) extends ReportStrategy {

  def name = "default"

  override def innerCreateReport(laps: List[LapLogEntry]): JsValue = {
    val lastLaps = lastLapFinder.map(laps)
    val sorted = lastLaps.sortBy(_.totalTime.getMillis)
    val reportItems = ((1 to sorted.size) zip sorted).map {
      case (position, lap) => Json.obj(
        "position" -> position,
        "pilotNumber" -> lap.lapEvent.carNumber,
        "name" -> lap.lapEvent.pilotName,
        "totalLaps" -> lap.lapEvent.lap,
        "totalTime" -> lap.totalTime.toString
      )
    }
    JsArray(reportItems)
  }
}

