package reports.strategies

import org.joda.time.Duration
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfterEach, MustMatchers }
import org.scalatestplus.play.PlaySpec

import play.api.libs.json.{ JsArray, JsValue }

import com.danielasfregola.randomdatagenerator.RandomDataGenerator._

import models.{ CumulativeLapEntry, LapLogEntry }
import reports.transforms.LastLapFinder


class TimeAfterWinnerReportStrategySpec extends PlaySpec with MustMatchers with MockitoSugar with BeforeAndAfterEach {

  val lastLapFinderMock = mock[LastLapFinder]
  val target = new TimeAfterWinnerReportStrategy(lastLapFinderMock)

  "Time after winner report" must {

    "Empty result given no lap entries" in {
      when(lastLapFinderMock.map(any())).thenReturn(Nil)
      target.innerCreateReport(Nil) must matchPattern {
        case JsArray(e) if e.isEmpty =>
      }
    }

    "Right report given some lap entries" in {
      val winner = CumulativeLapEntry(random[LapLogEntry].copy(pilotName = "topSpeed"), Duration.millis(10), 100.55)
      val last = CumulativeLapEntry(random[LapLogEntry].copy(pilotName = "turtleFit"), Duration.millis(100), 0.2D)
      val entries = List(winner, last)

      when(lastLapFinderMock.map(any())).thenReturn(entries)

      target.innerCreateReport(random[LapLogEntry](2).toList) must matchPattern {
        case JsArray(rank: Seq[JsValue])
          if rank.find(e => (e \ "name").as[String] == "turtleFit")
              .exists(e => (e \ "timeAfterWinner").as[String] == last.totalTime.minus(winner.totalTime).toString) =>
      }
    }
  }

  override def beforeEach(): Unit = {
    reset(lastLapFinderMock)
    super.beforeEach()
  }

}
