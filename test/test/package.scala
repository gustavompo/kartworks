import java.util.Base64

import scala.concurrent.Future

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers._
import org.scalatest._
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.Configuration
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.inject.ApplicationLifecycle
import play.api.inject.guice.GuiceApplicationBuilder

package object test {


  trait BaseSpec extends FlatSpec with MustMatchers with OptionValues with Inside with PatienceConfiguration with MockitoSugar{
    def matchArg[T](fn: T => Boolean) = argThat(new  ArgumentMatcher[T]{
      override def matches(argument: T): Boolean = fn(argument)
    })
  }

  trait FunctionalSpec extends BaseSpec with GuiceOneAppPerSuite {
    lazy val injector = app.injector
    lazy val config = injector.instanceOf[Configuration]

  }

  trait DAOSpec extends FunctionalSpec with BeforeAndAfterAll {
    this: TestSuite =>

    lazy val dbApi = injector.instanceOf[DBApi]

    override def fakeApplication() = new GuiceApplicationBuilder()
        .configure(
          "idwall.matrices.full-check" -> "",
          "idwall.matrices.fast-check" -> ""
        )

        .build()

    def fixtures: Option[String] = None

    override def beforeAll(): Unit = {
      val default = dbApi.database("default")
      // Apply the fixtures
      default.withConnection { conn =>
        fixtures map { sql =>
          conn.prepareStatement(sql).execute()
        }
      }
      // Cleanup after application ends
      injector.instanceOf[ApplicationLifecycle].addStopHook { () =>
        dbApi.databases() foreach { db =>
          Evolutions.cleanupEvolutions(db)
        }
        Future.successful(None)
      }
      super.beforeAll()
    }
  }

}

