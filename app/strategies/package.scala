import org.joda.time.Duration

import play.api.libs.json._



package object strategies {
  implicit val durationFormat = new OFormat[Duration] {
    override def reads(json: JsValue): JsResult[Duration] = JsSuccess(Duration.parse(json.as[String]))

    override def writes(o: Duration): JsObject = Json.obj("time" -> o.toString)
  }
//  implicit val cFormat = Json.format[CumulativeLapEvent]
}
