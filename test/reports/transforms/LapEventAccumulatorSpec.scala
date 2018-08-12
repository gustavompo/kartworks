package reports.transforms

import org.joda.time.Duration
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfterEach, MustMatchers }
import org.scalatestplus.play.PlaySpec

import models.{ CumulativeLapEntry, LapLogEntry }
import utils.ImplicitUtils._

class LapEventAccumulatorSpec extends PlaySpec with MustMatchers with MockitoSugar with BeforeAndAfterEach {
  val target = new LapEventAccumulator

  "Lap event accumulator" must {
    "accumulate total time and agerage speed over laps" in {
      val laps = List(
        LapLogEntry(time = "", carNumber = "001", pilotName = "dick vigarista", lap = 1, lapTime = "01:30.000", lapSpeed = 1d),
        LapLogEntry(time = "", carNumber = "001", pilotName = "dick vigarista", lap = 2, lapTime = "01:35.000", lapSpeed = 0.8)
      )
      val result = target.map(laps)

      val expectedTotalTimeDick = laps.map(e => Duration.millis(e.lapTime.timeStringAsMillis)).fold(Duration.ZERO)(_ plus _)
      val expectedAvgSpeedDick = (1d + 0.8) / 2

      result.find(_.lapEvent.lap == 2) must matchPattern {
        case Some(CumulativeLapEntry(_, duration, avgSpeed))
          if duration == expectedTotalTimeDick && avgSpeed == expectedAvgSpeedDick =>
      }
    }
  }
}
