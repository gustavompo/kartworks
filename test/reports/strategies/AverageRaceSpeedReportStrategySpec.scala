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


class AverageRaceSpeedReportStrategySpec extends PlaySpec with MustMatchers with MockitoSugar with BeforeAndAfterEach{

  val lastLapFinderMock = mock[LastLapFinder]
  val target = new AverageRaceSpeedReportStrategy(lastLapFinderMock)

  "Average race speed report" must {

    "Empty result given no lap entries" in {
      when(lastLapFinderMock.map(any())).thenReturn(Nil)
      target.innerCreateReport(Nil) must matchPattern{
        case JsArray(e) if e.isEmpty =>
      }
    }

    "Right report given some lap entries" in {
      val entries = List(
        CumulativeLapEntry(random[LapLogEntry].copy(pilotName = "topSpeed"), Duration.millis(10), 100.55),
        CumulativeLapEntry(random[LapLogEntry].copy(pilotName = "turtleFit"), Duration.millis(100), 0.2D)
      ).sortBy(_.avgSpeed).reverse

      when(lastLapFinderMock.map(any())).thenReturn(entries)

      target.innerCreateReport(Nil) must matchPattern{
        case JsArray(rank: Seq[JsValue])
          if (rank.head \ "name").as[String] == "topSpeed" &&
              (rank.head \ "averageRaceSpeed").as[Double] == entries.head.avgSpeed  =>
      }
    }
  }

  override def beforeEach(): Unit = {
    reset(lastLapFinderMock)
    super.beforeEach()
  }

}
