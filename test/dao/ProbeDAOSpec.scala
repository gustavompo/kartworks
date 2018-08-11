//package dao
//
//import java.util.UUID
//
//import adapters.ProductLine.Pop
//import models.Probe
//import org.scalatest.concurrent.PatienceConfiguration.Timeout
//import org.scalatest.concurrent.ScalaFutures._
//import org.scalatest.time.{Seconds, Span}
//import play.api.libs.json.Json
//import test.{DAOSpec, FunctionalTest}
//
//class ProbeDAOSpec extends DAOSpec {
//
//  val probeDAO = injector.instanceOf[ProbeDAO]
//  val timeout = Timeout(Span(2, Seconds))
//
//  val id = UUID.randomUUID()
//  val requestKey = UUID.randomUUID().toString
//  val documentId = "12345678900"
//
//  override def fixtures = Some(s"""
//    INSERT INTO PROBES (id, request_id, callback_url, request, response, executed_at, created_at, updated_at)
//    VALUES ('$id', '$requestKey', 'http://host.com', '{"parameters": {"identityNumber": $documentId}}', '{"result": 0}', NULL, NOW(), NOW());
//  """)
//
//  "#findById(id)" should "return some record" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findById(id), timeout) { result =>
//      result.value.id mustBe id
//    }
//  }
//
//  it should "return none if could not find" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findById(UUID.randomUUID())) { result =>
//      result mustBe None
//    }
//  }
//
//  it should "accept uuid as strings" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findById(id.toString), timeout) { result =>
//      result.value.id mustBe id
//    }
//  }
//
//  it should "return none if string isn't an uuid" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findById("bad-uuid")) { result =>
//      result mustBe None
//    }
//  }
//
//  "#insert(probe)" should "return the saved instance of the model with the current ID" taggedAs(FunctionalTest) in {
//    val probe = Probe(UUID.randomUUID().toString, "http://host.com", Json.obj())
//
//    whenReady(probeDAO.insert(probe)) { model =>
//      model mustBe a[Probe]
//      model.id mustBe a[UUID]
//    }
//  }
//
//  "#findByRequestKey(id)" should "return some record" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findByRequestKey(requestKey), timeout) { result =>
//      result.value.id mustBe id
//    }
//  }
//
//  it should "return none if could not find" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findByRequestKey(UUID.randomUUID().toString)) { result =>
//      result mustBe None
//    }
//  }
//
//  "#findIfRequestKeyExists" should "return true if a request key already exists in the database, false otherwise" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findIfRequestKeyExists(requestKey)) { result =>
//      result mustBe true
//    }
//    whenReady(probeDAO.findIfRequestKeyExists(UUID.randomUUID().toString)) { result =>
//      result mustBe false
//    }
//  }
//
//  "findIfProbeExistsForDocumentID" should "return 0 if the probe exists, empty otherwise" taggedAs(FunctionalTest) in {
//    whenReady(probeDAO.findByCPF(documentId)) { result =>
//      result mustBe Some(0)
//    }
//
//    whenReady(probeDAO.findByCPF("123")) { result =>
//      result mustBe None
//    }
//  }
//}
