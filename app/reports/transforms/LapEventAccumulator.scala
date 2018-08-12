package reports.transforms

import scala.concurrent.duration._
import utils.ImplicitUtils._

import org.joda.time.Duration

import javax.inject.Singleton
import models.{ CumulativeLapEntry, LapLogEntry }
import scalacache.CacheConfig._
import scalacache.caffeine.CaffeineCache
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

@Singleton
class LapEventAccumulator extends MiddleLayerTransform[LapLogEntry, CumulativeLapEntry] {
  implicit val cache = CaffeineCache[List[CumulativeLapEntry]]

  def map(xs: List[LapLogEntry]): List[CumulativeLapEntry] = memoizeSync(Some(2 minutes)) {
    val allCumulative = xs.foldLeft(Map[String, CumulativeLapEntry]()) { (acc, lapEvt) =>
      val curr = accumulateSum(acc.get(lapEvt.carNumber), lapEvt)
      acc + ((lapEvt.carNumber, curr))
    }
    allCumulative.values.toList.sortBy(_.totalTime.getMillis)
  }

  private def accumulateSum(previous: Option[CumulativeLapEntry], lapEvent: LapLogEntry) = previous match {
    case Some(rep) =>
      CumulativeLapEntry(lapEvent, rep.totalTime.plus(Duration.millis(lapEvent.lapTime.timeStringAsMillis)), (rep.avgSpeed + lapEvent.lapSpeed) / 2)
    case None =>
      CumulativeLapEntry(lapEvent, Duration.millis(lapEvent.lapTime.timeStringAsMillis), lapEvent.lapSpeed)
  }

}