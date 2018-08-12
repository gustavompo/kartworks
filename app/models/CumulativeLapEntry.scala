package models

import org.joda.time.Duration

case class CumulativeLapEntry(lapEvent: LapLogEntry, totalTime: Duration, avgSpeed: Double)
