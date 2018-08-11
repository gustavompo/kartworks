//package controllers
//
//import java.time.Instant
//import java.util.UUID
//
//import adapters.ProbeResponse
//import adapters.ProbeResponse.Result._
//import adapters.ProductLine.Pop
//import akka.Done
//import dao.ProbeDAO
//import models.Probe
//import org.mockito.ArgumentMatchers._
//import org.mockito.Mockito._
//import play.api.inject._
//import play.api.inject.guice.GuiceApplicationBuilder
//import play.api.libs.json.Json
//import play.api.test.Helpers._
//import play.api.test._
//import play.api.{ Application, mvc }
//import queues.SendToProviderQueue
//import test.{ FunctionalSpec, FunctionalTest }
//import fabricator.ProbeFabricator
//
//import scala.concurrent.{ Future, Promise }
//
//class ProbeControllerSpec extends FunctionalSpec {
//
//  val probe = Probe(UUID.randomUUID().toString, "http://host.com", Json.obj())
//
//  val stream = mock[SendToProviderQueue]
//
//  val probeDAO = mock[ProbeDAO]
//  val done = Promise[Done]
//  when(probeDAO.findIfRequestKeyExists(any[String])) thenReturn Future.successful(false)
//  when(stream.produce(any())(any())) thenReturn done.future
//
//  override def fakeApplication(): Application =
//    new GuiceApplicationBuilder()
//        .overrides(bind[ProbeDAO].to(probeDAO))
//        .overrides(bind[SendToProviderQueue].to(stream))
//        .build()
//
//  "POST /v1/probe" should "receive data with all fields filled, store and produce msg" taggedAs (FunctionalTest) in {
//
//    val zipCodes = Seq("05416000", "05416-000")
//
//    def makeRequest(zipCode: String, probeId: UUID): (Future[mvc.Result], Probe) = {
//      val requestKey = UUID.randomUUID().toString
//      val timestamp = Instant.now.getEpochSecond
//      val body = Json.parse(ProbeFabricator.createRequest(zipCode, requestKey, timestamp))
//      val probe = Probe(requestKey, "http://host.com", body, None, id = probeId)
//      when(probeDAO.insert(any[Probe])) thenReturn Future.successful(probe)
//
//      (route(app, FakeRequest(POST, s"/v1/probe")
//          .withHeaders(AUTHORIZATION -> s"Basic $authToken")
//          .withJsonBody(body)
//      ).get, probe)
//
//    }
//
//    // Mock insert response
//    for (zipCode <- zipCodes) {
//      val probeId = UUID.randomUUID()
//
//      val (response, probe) = makeRequest(zipCode, probeId)
//      status(response) mustBe CREATED
//      header("X-Probe-Id", response).value mustBe probeId.toString
//      verify(probeDAO).insert(refEq(probe, "id", "createdAt", "updatedAt"))
//      verify(stream).produce(probe)
//    }
//
//
//  }
//
//  it should "receive data only with required fields, store and produce msg" taggedAs (FunctionalTest) in {
//    val requestKey = UUID.randomUUID().toString
//    val timestamp = Instant.now.getEpochSecond
//    val body = Json.parse(
//      s"""
//      {
//        "requestKey": "$requestKey",
//        "productLine": "99pop",
//        "callbackUrl": "http://host.com",
//        "parameters": {
//          "fullName":"Luis Henrique Correa Vieira",
//          "birthday":$timestamp,
//          "identityNumber":"32167505825",
//          "mothersFullName":"Margarete Vieira",
//          "fathersFullName":"Nelson Vieira",
//          "identityDocumentNumber":"46277167",
//          "identityDocumentIssuingDate":$timestamp,
//          "identityDocumentIssuingState":"SP",
//          "identityDocumentIssuingSource":"SSP",
//          "drivesLicenseNumber":"0566568481"
//        }
//      }
//    """)
//
//    val probeId = UUID.randomUUID()
//    val probe = Probe(requestKey, "http://host.com", body, None, id = probeId)
//    when(probeDAO.insert(any[Probe])) thenReturn Future.successful(probe)
//
//    val response = route(app, FakeRequest(POST, s"/v1/probe")
//        .withHeaders(AUTHORIZATION -> s"Basic $authToken")
//        .withJsonBody(body)
//    ).get
//
//    // Mock insert response
//    status(response) mustBe CREATED
//    header("X-Probe-Id", response).value mustBe probeId.toString
//    verify(probeDAO).insert(refEq(probe, "id", "createdAt", "updatedAt"))
//    verify(stream).produce(probe)
//  }
//
//  it should "block access without authorization" taggedAs (FunctionalTest) in {
//    val response = route(app, FakeRequest(POST, s"/v1/probe")
//        .withJsonBody(Json.obj())
//    ).get
//    status(response) mustBe UNAUTHORIZED
//
//  }
//
//  it should "return a bad request without all required fields " taggedAs (FunctionalTest) in {
//    val timestamp = Instant.now.getEpochSecond
//    val body = Json.parse(
//      s"""
//      {
//        "productLine": "99pop",
//        "callbackUrl": "ftp://badurl",
//        "parameters": {
//          "gender":1,
//          "birthday":$timestamp,
//          "civilStatus":"SING",
//          "mothersFullName":"Margarete Vieira",
//          "fathersFullName":"Nelson Vieira",
//          "identityDocumentNumber":"46277167",
//          "identityDocumentIssuingDate":$timestamp,
//          "identityDocumentIssuingState":"SP",
//          "identityDocumentIssuingSource":"SSP",
//          "drivesLicenseSecurityCode":"12345678901"
//        }
//      }
//    """)
//
//    val response = route(app, FakeRequest(POST, s"/v1/probe")
//        .withHeaders(AUTHORIZATION -> s"Basic $authToken")
//        .withJsonBody(body)
//    ).get
//
//    status(response) mustBe BAD_REQUEST
//    val json = contentAsJson(response)
//    (json \ "errors" \ "requestKey").as[Seq[String]] must contain("This field is required")
//    (json \ "errors" \ "callbackUrl").as[Seq[String]] must contain("Invalid URL")
//    (json \ "errors" \ "parameters.fullName").as[Seq[String]] must contain("This field is required")
//    (json \ "errors" \ "parameters.identityNumber").as[Seq[String]] must contain("This field is required")
//    (json \ "errors" \ "parameters.drivesLicenseNumber").as[Seq[String]] must contain("This field is required")
//  }
//
//  it should "return a bad request if the request key already exists in the database" in {
//    val requestKey = UUID.randomUUID().toString
//    val timestamp = Instant.now.getEpochSecond
//    val body = Json.parse(
//      s"""
//      {
//        "requestKey": "$requestKey",
//        "productLine": "99pop",
//        "callbackUrl": "http://host.com",
//        "parameters": {
//          "fullName":"Luis Henrique Correa Vieira",
//          "gender":1,
//          "birthday":$timestamp,
//          "civilStatus":"SING",
//          "identityNumber":"32167505825",
//          "mothersFullName":"Margarete Vieira",
//          "fathersFullName":"Nelson Vieira",
//          "identityDocumentNumber":"46277167",
//          "identityDocumentVerificationDigit":"2",
//          "identityDocumentIssuingDate":$timestamp,
//          "identityDocumentIssuingState":"SP",
//          "identityDocumentIssuingSource":"SSP",
//          "drivesLicenseNumber":"0566568481",
//          "drivesLicenseSecurityCode":"0123456789",
//          "addressLine1":"Av. dos Bandeirantes",
//          "addressLine2":"3º andar",
//          "addressNumber":"460",
//          "addressZipCode":"04553-900",
//          "addressCity":"São Paulo",
//          "addressState":"SP",
//          "addressNeighborhood":"Brooklyn Paulista"
//        }
//      }
//    """)
//
//    when(probeDAO.findIfRequestKeyExists(requestKey)) thenReturn Future.successful(true)
//
//    val response = route(app, FakeRequest(POST, s"/v1/probe")
//        .withHeaders(AUTHORIZATION -> s"Basic $authToken")
//        .withJsonBody(body)
//    ).get
//
//    status(response) mustBe BAD_REQUEST
//    val json = contentAsJson(response)
//    (json \ "errors" \ "requestKey").as[Seq[String]] must contain("A probe already exists with this key")
//  }
//
//  it should "sanitize fields, store and produce a message" in {
//    val requestKey = UUID.randomUUID().toString
//    val driverLicenseNumber = ",  05. 483 30-314  "
//    val identityNumber = " 037.234, 03-182  "
//    val body = Json.parse(ProbeFabricator.createRequest(requestKey, driverLicenseNumber, identityNumber))
//
//      val response = route(app, FakeRequest("POST", "/v1/probe")
//        .withHeaders("AUTHORIZATION" -> s"Basic $authToken")
//        .withJsonBody(body)
//      ).get
//
//      status(response) mustBe CREATED
//  }
//
//  "GET /v1/probe/:requestId  " should "return a response object " taggedAs (FunctionalTest) in {
//    val requestId = UUID.randomUUID().toString
//    val body = Json.parse(
//      s"""
//      {
//        "id": "${requestId}"
//      }
//    """)
//
//    val probeResponse = ProbeResponse(requestId, Approved, Seq.empty)
//    val probe = Probe(requestId, "http://host.com", request = Json.obj(), response = Some(Json.toJson(probeResponse)), None)
//    when(probeDAO.findByRequestKey(requestId)) thenReturn Future.successful((Some(probe)))
//
//    val response = route(app, FakeRequest(GET, s"/v1/probe/${requestId}")
//        .withHeaders(AUTHORIZATION -> s"Basic $authToken")
//    ).get
//
//    status(response) mustBe OK
//    val json = contentAsJson(response)
//    (json \ "requestKey").as[String] mustBe requestId
//  }
//
//  it should "return no content" taggedAs (FunctionalTest) in {
//    val requestId = UUID.randomUUID().toString
//    when(probeDAO.findByRequestKey(requestId)) thenReturn Future.successful(None)
//
//    val response = route(app, FakeRequest(GET, s"/v1/probe/${requestId}")
//        .withHeaders(AUTHORIZATION -> s"Basic $authToken")
//    ).get
//
//    status(response) mustBe NOT_FOUND
//  }
//
//}
