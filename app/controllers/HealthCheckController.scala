package controllers

import play.api.mvc._

import javax.inject._

@Singleton
class HealthCheckController extends InjectedController {
  def healthCheck = Action {
    Ok("I'm alive!")
  }
}
