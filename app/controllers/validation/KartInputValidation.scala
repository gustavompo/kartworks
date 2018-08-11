package controllers.validation

import controllers.LapEvent
import javax.inject.Inject

@Inject
class KartInputValidation {
  val timePattern = """\d{2}\:\d{2}\:\d{2}\.\d{3}"""
  val carAndPilotPattern = """\d{3}\s.\s\w*\.*\w*"""
  val lapPattern = """\d+"""
  val lapTimePatter = """\d+\:\d{2}\.\d{3}"""
  val avgSpeedPatter = """\d+\,*\d*"""

  val columnSeparator = """\s{2,}"""

  def parseValidate(input: String): Either[String, List[LapEvent]] = {
    input
        .split("\n")
        .tail
        .foldLeft[Either[String, List[LapEvent]]](Right(List[LapEvent]()))(reducer)
  }

  private def reducer(eventsE: Either[String, List[LapEvent]], line: String) = for {
      events <- eventsE
      lapEvent <- lineExtractor(line)
    } yield events :+ lapEvent


  private def lineExtractor(line: String) = {
    val linePattern = List(timePattern, carAndPilotPattern, lapPattern, lapTimePatter, avgSpeedPatter)
          .map { p => s"($p)" }
          .mkString(columnSeparator).r

    line match {
      case linePattern(time, carAndPilot, l, t, a) =>
        val cp = carAndPilot.split("\\s")
        Right(LapEvent(time, cp.head, cp.last, l.toInt, t, a.replace(',', '.').toDouble))

      case _ =>
        Left(s"line does not match the expected pattern ($line)")
    }
  }
}
