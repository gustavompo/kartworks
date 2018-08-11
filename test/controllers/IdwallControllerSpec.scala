//package controllers
//
//import java.util.UUID
//
//import akka.Done
//import dao.ProviderRequestDAO
//import models.ProviderRequest
//import org.mockito.ArgumentMatchers.{ any, refEq }
//import org.mockito.Mockito.{ spy, verify, when }
//import org.scalatest.concurrent.PatienceConfiguration.Timeout
//import org.scalatest.concurrent.ScalaFutures.whenReady
//import org.scalatest.time.{ Seconds, Span }
//import play.api.inject.bind
//import play.api.inject.guice.GuiceApplicationBuilder
//import play.api.libs.json.Json
//import play.api.test.Helpers._
//import play.api.test._
//import services.providers.ProviderService.ProviderName.Idwall
//import queues.FetchFromProviderQueue
//import test.{ FunctionalSpec, FunctionalTest }
//
//import scala.concurrent.Future
//
//class IdwallControllerSpec extends FunctionalSpec {
//
//  val stream = mock[FetchFromProviderQueue]
//
//  val dao = mock[ProviderRequestDAO]
//
//  val SECRET_TOKEN = "Secret-Token"
//
//  override def fakeApplication() = new GuiceApplicationBuilder()
//        .overrides(bind[ProviderRequestDAO].to(dao))
//        .overrides(bind[FetchFromProviderQueue].to(stream))
//        .build()
//
//  "POST /provider/idwall/webhook" should "receive data, check and produce msg" taggedAs(FunctionalTest) in {
//    val queryProtocol = "f72eee47-0b87-4f81-bd78-9c4e1deaeb12"
//    val expectedStatus = "CONCLUIDO"
//    val request = ProviderRequest(UUID.randomUUID(), Idwall, queryProtocol, Some(Json.obj()), status = Some(expectedStatus))
//    when(dao.findByProviderId(queryProtocol)) thenReturn Future.successful(Some(request))
//    when(dao.save(any[ProviderRequest])) thenReturn Future.successful(1)
//
//    val body = Json.parse(
//      s"""{
//         |"dados": {
//         | "protocolo":
//         |  "${queryProtocol}",
//         |   "status": "${expectedStatus}"
//         |   },
//         |"tipo": "protocolo_status"
//         |}""".stripMargin)
//
//
//    val response = route(app, FakeRequest(POST, s"/v1/provider/idwall/webhook")
//        .withHeaders(SECRET_TOKEN -> UUID.randomUUID().toString)
//        .withJsonBody(body)
//    ).get
//
//    status(response) mustBe ACCEPTED
//    verify(dao).findByProviderId(request.providerId)
//    verify(stream).produce(refEq(request, "processedAt", "createdAt", "updatedAt"))(any())
//  }
//
//  it should "return bad request to invalid post" taggedAs(FunctionalTest) in {
//    val body = Json.parse(s"""
//      {
//        "evento": "PROTOCOLO_CONCLUIDO",
//        "data": {
//          "situacao": "CONCLUIDO",
//          "criadoEm": "2017-05-12T15:46:55.815"
//        },
//        "criadoEm": "2017-06-13T02:58:57.298",
//        "mensagem": "Protocolo terminou de processar"
//      }
//    """)
//
//    val response = route(app, FakeRequest(POST, s"/v1/provider/idwall/webhook")
//        .withHeaders(SECRET_TOKEN -> UUID.randomUUID().toString)
//        .withJsonBody(body)
//    ).get
//
//    status(response) mustBe BAD_REQUEST
//  }
//
//  it should "return an not acceptable when the Secret-Token is not sent" in {
//    val response = route(app, FakeRequest(POST, s"/v1/provider/idwall/webhook")
//      .withJsonBody(Json.obj())
//    ).get
//
//    status(response) mustBe NOT_ACCEPTABLE
//  }
//
//  it should "save callback status in table" in {
//    val timeout = Timeout(Span(2, Seconds))
//    val queryProtocol = "f7555e47-0b87-4f81-bd78-9c4e1deaeb12"
//    val expectedStatus = "CONCLUIDO"
//    val request = ProviderRequest(UUID.randomUUID(), Idwall, queryProtocol, Some(Json.obj()), status = Some(expectedStatus))
//    when(dao.findByProviderId(queryProtocol)) thenReturn Future.successful(Some(request))
//    when(dao.save(any[ProviderRequest])) thenReturn Future.successful(1)
//
//    val body = Json.parse(
//      s"""{
//         |"dados": {
//         | "protocolo":
//         |  "${queryProtocol}",
//         |   "status": "${expectedStatus}"
//         |   },
//         |"tipo": "protocolo_status"
//         |}""".stripMargin)
//
//
//    val response = route(app, FakeRequest(POST, s"/v1/provider/idwall/webhook")
//        .withHeaders(SECRET_TOKEN -> UUID.randomUUID().toString)
//        .withJsonBody(body)
//    ).get
//
//    status(response) mustBe ACCEPTED
//    verify(dao).findByProviderId(request.providerId)
//    verify(stream).produce(refEq(request, "processedAt", "createdAt", "updatedAt"))(any())
//    whenReady(dao.findByProviderId(request.providerId), timeout) { result =>
//      result.value.providerName mustBe Idwall
//      result.value.status mustBe Some(expectedStatus)
//    }
//  }
//}
