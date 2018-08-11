package strategies

import scala.concurrent.duration._

import org.joda.time.Duration

import controllers.LapEvent
import javax.inject.Inject
import scalacache.CacheConfig._
import scalacache.caffeine.CaffeineCache
import scalacache.memoization._
import scalacache.modes.sync._

trait MiddlewareReportTransform[I, O] {
  def map(items: List[I]): List[O]
}

@Inject
class LapEventAccumulator extends MiddlewareReportTransform[LapEvent, CumulativeLapEvent] {
  implicit val cache = CaffeineCache[List[CumulativeLapEvent]]

  def map(xs: List[LapEvent]): List[CumulativeLapEvent] = memoizeSync(Some(2 minutes)) {
    val allCumulative = xs.foldLeft(Map[String, CumulativeLapEvent]()) { (acc, lapEvt) =>
      val curr = accumulateSum(acc.get(lapEvt.carNumber), lapEvt)
      acc + ((lapEvt.carNumber, curr))
    }
    allCumulative.values.toList.sortBy(_.totalTime.getMillis)
  }

  private def accumulateSum(previous: Option[CumulativeLapEvent], lapEvent: LapEvent) = previous match {
    case Some(rep) =>
      CumulativeLapEvent(lapEvent, rep.totalTime.plus(Duration.millis(parseTime(lapEvent.lapTime))), (rep.avgSpeed + lapEvent.lapSpeed) / 2)
    case None =>
      CumulativeLapEvent(lapEvent, Duration.millis(parseTime(lapEvent.lapTime)), lapEvent.lapSpeed)
  }

  private def parseTime(str: String) = {
    val spl = str.split(':')
    val sMil = spl(1).split('.')
    spl(0).toLong * 60 * 1000 + sMil(0).toLong * 1000 + sMil(1).toLong

  }
}

@Inject
class LastLapFinder(
    accumulator: LapEventAccumulator
) extends MiddlewareReportTransform[LapEvent, CumulativeLapEvent] {
  implicit val cache = CaffeineCache[List[CumulativeLapEvent]]

  override def map(items: List[LapEvent]): List[CumulativeLapEvent] = memoizeSync(Some(2 minutes)) {
    val accItems = accumulator.map(items)
    val lapsByCar = accItems.groupBy(_.lapEvent.carNumber)
    val winnerLap = accItems.find(_.lapEvent.lap == 4)
    val lastLapOfEachDriver = lapsByCar.flatMap { case (_, laps) =>
      laps.find(lp => winnerLap.exists(wl => lp.totalTime.getMillis > wl.totalTime.getMillis))
    }.toList

    winnerLap.map(lastLapOfEachDriver :+ _).getOrElse(lastLapOfEachDriver)
  }
}