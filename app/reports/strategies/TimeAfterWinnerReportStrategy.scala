package reports.strategies

import play.api.libs.json.{ JsArray, JsValue, Json }

import com.google.inject.multibindings.ProvidesIntoSet

import javax.inject.Inject
import models.LapLogEntry
import reports.transforms.LastLapFinder

@ProvidesIntoSet
class TimeAfterWinnerReportStrategy @Inject()(
    lastLapFinder: LastLapFinder
) extends ReportStrategy {

  def name = "time-after-winner"

  override def innerCreateReport(laps: List[LapLogEntry]): JsValue = {
    if (laps.isEmpty) JsArray(Nil)
    else {
      val lastLaps = lastLapFinder.map(laps).sortBy(_.totalTime.getMillis)
      val winner = lastLaps.head
      val reportItems = lastLaps.map { lap =>
        Json.obj(
          "pilotNumber" -> lap.lapEvent.carNumber,
          "name" -> lap.lapEvent.pilotName,
          "timeAfterWinner" -> lap.totalTime.minus(winner.totalTime).toString
        )
      }
      JsArray(reportItems)
    }
  }
}

