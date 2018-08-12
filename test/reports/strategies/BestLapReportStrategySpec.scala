package reports.strategies

import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec

import play.api.libs.json.{ JsArray, JsObject }

import com.danielasfregola.randomdatagenerator.RandomDataGenerator._

import models.LapLogEntry


class BestLapReportStrategySpec extends PlaySpec with MustMatchers {

  val target = new BestLapReportStrategy

  "Best lap report" must {

    "Empty result given no lap entries" in {
      target.innerCreateReport(Nil) must matchPattern {
        case JsArray(e) if e.isEmpty =>
      }
    }

    "Right report given some lap entries" in {
      val entries = List(
        random[LapLogEntry].copy(lapSpeed = 1D),
        random[LapLogEntry].copy(lapSpeed = 2D),
        random[LapLogEntry].copy(lapSpeed = 3D)
      ).sortBy(_.lapSpeed).reverse

      val res = target.innerCreateReport(entries)
      res must matchPattern {
        case o: JsObject if (o \ "name").as[String] == entries.head.pilotName =>
      }
    }
  }

}
