package controllers.validation

import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import org.scalatest.Inspectors._

import models.LapLogEntry


class LapLogEntryValidationSpec extends PlaySpec with MustMatchers {

  val target = new LapLogEntryValidation

  "LogEntry validation" must {

    "Left(error) given too few lines" in {
      val inputs = List(
        "only header",
        "header\n\n\n",
        "\n\n    \n\n    \n\n"
      )
      forAll(inputs.map(target.parseValidate)) { _ must matchPattern {
          case Left("Invalid request, must include header and lap entries") =>
        }
      }
    }

    "Left(error) given a row with invalid syntax" in {
      val input = "header\ninvalidContent"
      target.parseValidate(input) must matchPattern {
        case Left(err: String) if err.startsWith("line does not match the expected pattern")=>
      }
    }

    "Right(values) given valid content syntax" in {
      val input =
        """|notUsedHeaders
           |00:00:00.000  001 – Anakin   10   0:00.001   100
           |00:00:00.000  002 – ObiWan   10   0:00.002   50
      """.stripMargin
      target.parseValidate(input) must matchPattern {
        case Right(lapEntries: List[LapLogEntry]) if lapEntries.size == 2 && lapEntries.head.pilotName == "Anakin" =>
      }
    }
  }

}
