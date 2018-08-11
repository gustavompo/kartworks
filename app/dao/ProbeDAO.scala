//package dao
//
//import java.time.Instant
//import java.util.UUID
//
//import scala.concurrent.{ ExecutionContext, Future }
//import scala.util.Try
//
//import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
//import play.api.libs.json.JsValue
//
//import dao.ProbeDAO.ProbeTable
//import db.profile.ExtendedPostgresProfile
//import javax.inject.Inject
//import models.Probe
//
//
//class ProbeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
//                                  (implicit ec: ExecutionContext) extends ProbeTable {
//  import profile.api._
//
//
//  def findById(id: UUID) = db.run {
//    probeTable.filter(_.id === id).result.headOption
//  }
//
//  def findById(id: String): Future[Option[Probe]] = Try(UUID.fromString(id)).toOption match {
//    case Some(uuid) => db.run {
//      probeTable.filter(_.id === uuid).result.headOption
//    }
//    case None =>
//      Future.successful(None)
//  }
//
//  def insert(probe: Probe) = db.run {
//    (probeTable returning probeTable.map(_.id) into ((user, id) => user.copy(id = id))) += probe
//  }
//
//  def save(probe: Probe) = db.run {
//    probeTable.insertOrUpdate(probe)
//  }
//
//  def findByRequestKey(requestId: String) = db.run {
//    probeTable.filter(_.requestId === requestId).result.headOption
//  }
//
//  def findIfRequestKeyExists(requestId: String) = db.run {
//    probeTable.filter(_.requestId === requestId).size.result.map(_ > 0)
//  }
//
//  def findByCPF(documentId: String) = {
//    // TODO change query to remove the hard coded id
//      val query =
//        sql"""
//          select
//            response -> 'result'
//          from probes
//          where
//            request -> 'parameters' ->> 'identityNumber' = $documentId
//          order by updated_at desc
//        """.as[Int].headOption
//
//      db.run(query)
//  }
//
//
//  def listByRequestKey(starfallRequestKey: String) = { db.run {
//    probeTable.filter(_.requestId === starfallRequestKey).result
//    }
//  }
//
//}
//
//object ProbeDAO {
//  private[dao] trait ProbeTable extends HasDatabaseConfigProvider[ExtendedPostgresProfile] {
//    import profile.api._
//
//    protected val probeTable = TableQuery[ProbeTable]
//
//    protected[ProbeTable] class ProbeTable(tag: Tag) extends Table[Probe](tag, "probes") {
//
//      def id = column[UUID]("id", O.PrimaryKey)
//      def requestId = column[String]("request_id")
//      def request = column[JsValue]("request")
//      def response = column[Option[JsValue]]("response")
//      def callbackUrl = column[String]("callback_url")
//      def executedAt = column[Option[Instant]]("executed_at")
//      def sentToProviderAt = column[Option[Instant]]("sent_to_provider_at")
//      def createdAt = column[Instant]("created_at")
//      def updatedAt = column[Instant]("updated_at")
//
//      def * = (requestId, callbackUrl, request,  response, executedAt, sentToProviderAt, createdAt, updatedAt, id) <>
//        ((Probe.apply _).tupled, Probe.unapply)
//    }
//  }
//}
