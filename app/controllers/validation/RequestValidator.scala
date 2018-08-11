package controllers.validation

import play.api.data.validation._

trait RequestValidator {
  lazy val zipCodeConstraint = Constraints.pattern("""^(\d{5})(-)?(\d{3})""".r, error = "Invalid ZIP code")
  lazy val driversLicenseSecurityCodeConstraint = Constraints.pattern("""^\d{11}$""".r, error = "Invalid driver license security code")
  lazy val driversLicenseNumberConstraint = Constraints.pattern("""^\d{8,11}$""".r, error = "Invalid driver license number")
  lazy val issuingStateConstraint = Constraints.pattern("""^[A-Z]{2}$""".r, error = "Invalid issuing source. Expected lenght=2")
  lazy val urlConstraint = Constraints.pattern("""^(https?)://[^\s/$.?#].[^\s]*$""".r, error = "Invalid URL")
  lazy val cpfConstraint = Constraints.pattern("""^\d{11}$""".r, error = "Invalid CPF")
}

object RequestValidator extends RequestValidator
