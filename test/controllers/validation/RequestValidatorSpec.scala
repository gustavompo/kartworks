//package controllers.validation
//
//import test.BaseSpec
//
//import play.api.data._
//import play.api.data.Forms._
//import play.api.data.validation._
//import play.api.data.validation.ValidationError
//import controllers.validation.RequestValidator._
//
//class RequestValidatorSpec extends BaseSpec {
//
//  def extractErrors(invalidList: Seq[Invalid]) = {
//    val messages = for {
//      invalid <- invalidList
//      validation <- invalid.errors
//    } yield validation.messages
//    messages.flatten
//  }
//
//  "ZipCode Validation" should "be valid with not formatatted value" in {
//    zipCodeConstraint("04686001") mustBe Valid
//  }
//
//  it should "be valid with formatatted value" in {
//    zipCodeConstraint("04686-001") mustBe Valid
//  }
//
//  it should "be invalid with invalid random codes" in {
//    val invalidList = List("", "101", "1100929", "1101A", "99taxiss", "039470111", "SaoPaulo")
//      .map(zipCodeConstraint(_).asInstanceOf[Invalid])
//
//    val result = extractErrors(invalidList)
//    result must contain ("Invalid ZIP code")
//  }
//
//  "Drivers License Security Code Validation" should "be valid" in {
//    driversLicenseSecurityCodeConstraint("00000001406") mustBe Valid
//    driversLicenseSecurityCodeConstraint("11111111111") mustBe Valid
//    driversLicenseSecurityCodeConstraint("92833902938") mustBe Valid
//  }
//
//  it should "be invalid with invalid random codes" in {
//    val expected = Seq(ValidationError("Invalid driver license security code"))
//    val invalidList = List("", "101", "1100929", "1101A", "99taxiss", "0394701110H", "SaoPauloSP!")
//      .map(driversLicenseSecurityCodeConstraint(_).asInstanceOf[Invalid])
//
//    val result = extractErrors(invalidList)
//    result must contain ("Invalid driver license security code")
//  }
//
//  "Drivers License Number Validation" should "be valid" in {
//    driversLicenseNumberConstraint("92833902") mustBe Valid
//    driversLicenseNumberConstraint("928339029") mustBe Valid
//    driversLicenseNumberConstraint("9283390298") mustBe Valid
//    driversLicenseNumberConstraint("00000001406") mustBe Valid
//  }
//
//  it should "be invalid with invalid random numbers" in {
//    val invalidList = List("", "99taxiss", "0394701110H", "SaoPauloSP!", "120192839102", "1928394", "1928464273464")
//      .map(driversLicenseNumberConstraint(_).asInstanceOf[Invalid])
//
//    val result = extractErrors(invalidList)
//    result must contain ("Invalid driver license number")
//  }
//
//  "Issuing State Validation" should "be valid" in {
//    issuingStateConstraint("SP") mustBe Valid
//    issuingStateConstraint("RJ") mustBe Valid
//    issuingStateConstraint("MA") mustBe Valid
//    issuingStateConstraint("BA") mustBe Valid
//  }
//
//  it should "be invalid with invalid random issuing states" in {
//    val invalidList = List("", "99", "ASD", "sp", "Rj", "mA", "RR ")
//      .map(issuingStateConstraint(_).asInstanceOf[Invalid])
//
//    val result = extractErrors(invalidList)
//    result must contain ("Invalid issuing source. Expected lenght=2")
//  }
//
//  "Url Validation" should "be valid" in {
//    urlConstraint("http://teste.com") mustBe Valid
//    urlConstraint("https://teste.com") mustBe Valid
//    urlConstraint("http://teste.com.ninja") mustBe Valid
//    urlConstraint("https://teste.com.ninja") mustBe Valid
//  }
//
//  it should "be invalid with invalid random urls" in {
//    val invalidList = List("", "9https://teste.com", "htt://teste.com", "://teste.com", "teste.com")
//      .map(urlConstraint(_).asInstanceOf[Invalid])
//
//    val result = extractErrors(invalidList)
//    result must contain ("Invalid URL")
//  }
//
//  "Cpf Validation" should "be valid" in {
//    cpfConstraint("11011011028") mustBe Valid
//    cpfConstraint("00022293844") mustBe Valid
//  }
//
//  it should "be invalid with invalid random cpfs" in {
//    val invalidList = List("", "044949288-11", "A121212121B", "11223344556 ")
//      .map(cpfConstraint(_).asInstanceOf[Invalid])
//
//    val result = extractErrors(invalidList)
//    result must contain ("Invalid CPF")
//  }
//
//}
//
