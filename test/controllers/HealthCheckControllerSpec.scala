package controllers

import play.api.test.Helpers._
import play.api.test._

import test.FunctionalSpec

class HealthCheckControllerSpec extends FunctionalSpec {

  "GET /healthCheck" should "return 200 ok and content I'm alive! " in {
    val home = route(app, FakeRequest(GET, "/healthCheck")).get

    status(home) mustBe OK
    contentType(home) mustBe Some("text/plain")
    contentAsString(home) must include("I'm alive!")
  }

  it should "send not found on a bad request" in {
    val error = route(app, FakeRequest(GET, "/foo")).get

    status(error) mustBe NOT_FOUND
  }

  "GET /info" should "return the application version" in {
    val info = route(app, FakeRequest(GET, "/info")).get

    status(info) mustBe OK
    contentType(info) mustBe Some("application/json")
    contentAsString(info) must include("version")
  }
}
