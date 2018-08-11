package strategies

import org.joda.time.Duration

import play.api.libs.json.{ JsArray, JsValue, Json }

import controllers.LapEvent
import javax.inject.Inject


case class CumulativeLapEvent(lapEvent: LapEvent, totalTime: Duration, avgSpeed: Double)

@Inject
class DefaultReportStrategy(
    lastLapFinder: LastLapFinder
) extends ReportStrategy {

  def name = "default"

  override def innerCreateReport(cumulativeEvents: List[LapEvent]): JsValue = {
    val lastLaps = lastLapFinder.map(cumulativeEvents)
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

