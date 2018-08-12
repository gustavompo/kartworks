package reports.strategies

import play.api.libs.json.{ JsArray, JsValue, Json }

import com.google.inject.multibindings.ProvidesIntoSet

import javax.inject.Inject
import models.LapLogEntry
import reports.transforms.LastLapFinder

@ProvidesIntoSet
class AverageRaceSpeedReportStrategy @Inject()(
    lastLapFinder: LastLapFinder
) extends ReportStrategy {

  def name = "average-speed"

  override def innerCreateReport(laps: List[LapLogEntry]): JsValue = {
    val lastLaps = lastLapFinder.map(laps).sortBy(_.avgSpeed).reverse
    val reportItems = lastLaps.map { lap =>
      Json.obj(
        "pilotNumber" -> lap.lapEvent.carNumber,
        "name" -> lap.lapEvent.pilotName,
        "averageRaceSpeed" -> lap.avgSpeed
      )
    }
    JsArray(reportItems)
  }
}

