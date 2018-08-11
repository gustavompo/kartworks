package db.profile

import java.time.Instant

import slick.jdbc.JdbcProfile

import com.github.tminglei.slickpg.{ ExPostgresProfile, PgDate2Support, PgEnumSupport, PgPlayJsonSupport }

trait ExtendedPostgresProfile extends ExPostgresProfile
  with PgEnumSupport with PgDate2Support with PgPlayJsonSupport { this: JdbcProfile =>

  def pgjson = "jsonb"

  override val api = new CustomAPI()

  class CustomAPI extends API with DateTimeImplicits with JsonImplicits {
    implicit val instantTypeMapper = new GenericDateJdbcType[Instant]("instant", java.sql.Types.TIMESTAMP)
//    implicit val providerNameTypeMapper = createEnumJdbcType("providerName", ProviderName)
//    implicit val productLineTypeMapper = createEnumJdbcType[ProductLine]("productLine", e => e.toString,
//      s => ProductLine.withName(s), quoteName = false)
  }
}

object ExtendedPostgresProfile extends ExtendedPostgresProfile
