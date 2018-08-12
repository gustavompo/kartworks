package models

case class LapLogEntry(
    time: String,
    carNumber: String,
    pilotName: String,
    lap: Int,
    lapTime: String,
    lapSpeed: Double
)