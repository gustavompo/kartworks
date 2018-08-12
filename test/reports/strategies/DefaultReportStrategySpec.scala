package reports.strategies

import org.joda.time.Duration
import org.scalatest.{ BeforeAndAfterEach, MustMatchers }
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import models.{ CumulativeLapEntry, LapLogEntry }
import reports.transforms.LastLapFinder
import com.danielasfregola.randomdatagenerator.RandomDataGenerator._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

import play.api.libs.json.{ JsArray, JsValue }



class DefaultReportStrategySpec extends PlaySpec with MustMatchers with MockitoSugar with BeforeAndAfterEach{

  val lastLapFinderMock = mock[LastLapFinder]
  val target = new DefaultReportStrategy(lastLapFinderMock)

  "Default report" must {

    "Empty result given no lap entries" in {
      when(lastLapFinderMock.map(any())).thenReturn(Nil)
      target.innerCreateReport(Nil) must matchPattern{
        case JsArray(e) if e.isEmpty =>
      }
    }

    "Right report given some lap entries" in {
      val cumulativeEntries = List(
        CumulativeLapEntry(random[LapLogEntry].copy(pilotName = "winner"), Duration.millis(10), 1D),
        CumulativeLapEntry(random[LapLogEntry].copy(pilotName = "loser"), Duration.millis(100), 1D)
      )

      val inputEntries = random[LapLogEntry](3).toList
      when(lastLapFinderMock.map(inputEntries)).thenReturn(cumulativeEntries)

      target.innerCreateReport(inputEntries) must matchPattern{
        case JsArray(rank: Seq[JsValue]) if (rank.head \ "name").as[String] == "winner" =>
      }
    }

    "Right report given a single lap entry" in {
      val entries = List(
        CumulativeLapEntry(random[LapLogEntry].copy(pilotName = "lonely-pilot"), Duration.millis(10), 1D)
      )
      when(lastLapFinderMock.map(any())).thenReturn(entries)

      target.innerCreateReport(Nil) must matchPattern{
        case JsArray(rank: Seq[JsValue]) if (rank.head \ "name").as[String] == "lonely-pilot" =>
      }
    }

  }

  override def beforeEach(): Unit = {
    reset(lastLapFinderMock)
    super.beforeEach()
  }

}
