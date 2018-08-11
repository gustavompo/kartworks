package controllers

import play.api.libs.json.Json

package object validation {
  implicit val eventFormat = Json.format[LapEvent]
}
