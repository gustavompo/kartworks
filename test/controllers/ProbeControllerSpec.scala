package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._

import akka.stream.{ ActorMaterializer, Materializer }

import play.api.Application
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{ Action, Results }
import play.api.test.Helpers._
import play.api.test._

import controllers.validation.{ LapLogEntryValidation, StrategiesNameValidation }
import models.LapLogEntry
import reports.strategies.ReportStrategy
import reports.transforms.LastLapFinder
import test.FunctionalSpec
import scala.collection.JavaConverters._

class ProbeControllerSpec extends FunctionalSpec with Injecting {

  val lapLogEntryValidationMock = mock[LapLogEntryValidation]
  val lastLapFinderMock = mock[LastLapFinder]
  val strategiesNameValidationMock = mock[StrategiesNameValidation]
  val strategiesMock = List[ReportStrategy]().toSet.asJava

  when(lapLogEntryValidationMock.parseValidate(any[String])) thenReturn Left("err")
  implicit lazy val materializer: Materializer = NoMaterializer
  lazy val bp = Helpers.stubPlayBodyParsers
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
//        .overrides(bind[KartReportController].to(new KartReportController(
//          lapLogEntryValidationMock,
//          bp,
//          lastLapFinderMock,
//          strategiesNameValidationMock,
//          strategiesMock
//        )))
        .build()


  "POST with invalid strategy" should "asd" in {
    val req = FakeRequest(POST, s"/api/kart/report/default").withTextBody("")
    val res = route(app, req).get
    status(res) mustBe NOT_FOUND
    println(contentAsString(res))
  }


}
