import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder

import net.codingwell.scalaguice.ScalaModule
import reports.strategies._

class Module extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    val multibinder = Multibinder.newSetBinder(binder, classOf[ReportStrategy])
    multibinder.addBinding().to(classOf[DefaultReportStrategy])
    multibinder.addBinding().to(classOf[BestLapReportStrategy])
    multibinder.addBinding().to(classOf[BestPilotLapReportStrategy])
    multibinder.addBinding().to(classOf[AverageRaceSpeedReportStrategy])
    multibinder.addBinding().to(classOf[TimeAfterWinnerReportStrategy])
  }
}