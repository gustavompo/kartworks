package reports.transforms

import scala.concurrent.duration._

import javax.inject.{ Inject, Singleton }
import models.{ CumulativeLapEntry, LapLogEntry }
import scalacache.CacheConfig._
import scalacache.caffeine.CaffeineCache
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

/**
  * There is no explicit documentation on the test for exactly what is the end of the race for each pilot.
  * Here it's assumed that, given the winner finished the race (a log entry for the 4th lap), all other pilots will still finish their last lap and that lap will be considered in all calculations
  */
@Singleton
class LastLapFinder @Inject()(
    accumulator: LapEventAccumulator
) extends MiddlewareReportTransform[LapLogEntry, CumulativeLapEntry] {
  implicit val cache = CaffeineCache[List[CumulativeLapEntry]]

  override def map(laps: List[LapLogEntry]): List[CumulativeLapEntry] = memoizeSync(Some(2 minutes)) {
    val cumulativeLaps = accumulator.map(laps)
    val lapsGroupedByCar = cumulativeLaps.groupBy(_.lapEvent.carNumber)
    val winnerLap = cumulativeLaps.find(_.lapEvent.lap == 4)
    val lastLapOfEachDriver = lapsGroupedByCar.flatMap { case (_, lapsOfCar) =>
      lapsOfCar.find(lp => winnerLap.exists(wl => lp.totalTime.getMillis > wl.totalTime.getMillis))
    }.toList

    winnerLap.map(lastLapOfEachDriver :+ _).getOrElse(lastLapOfEachDriver)
  }
}