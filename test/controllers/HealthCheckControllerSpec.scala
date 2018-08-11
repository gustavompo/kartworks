package controllers

import play.api.test.Helpers._
import play.api.test._
import test.{FunctionalSpec, FunctionalTest}

class HealthCheckControllerSpec extends FunctionalSpec {

  "GET /healthCheck" should "return 200 ok and content I'm alive! " taggedAs(FunctionalTest) in {
    val home = route(app, FakeRequest(GET, "/healthCheck")).get

    status(home) mustBe OK
    contentType(home) mustBe Some("text/plain")
    contentAsString(home) must include ("I'm alive!")
  }

  it should "send not found on a bad request" taggedAs(FunctionalTest) in  {
    val error = route(app, FakeRequest(GET, "/foo")).get

    status(error) mustBe NOT_FOUND
  }

  "GET /info" should "return the application version" taggedAs(FunctionalTest) in {
    val info = route(app, FakeRequest(GET, "/info")).get

    status(info) mustBe OK
    contentType(info) mustBe Some("application/json")
    contentAsString(info) must include ("version")
  }
}
