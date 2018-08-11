package filters

import javax.inject.Inject

import akka.util.ByteString
import play.api.libs.streams.Accumulator
import play.api.mvc._

import scala.concurrent.ExecutionContext

class PragmaFilter @Inject() (implicit ec: ExecutionContext) extends EssentialFilter {

  def apply(next: EssentialAction): EssentialAction = new EssentialAction {
    def apply(requestHeader: RequestHeader): Accumulator[ByteString, Result] = {
      if (requestHeader.path.equals("/docs/") || requestHeader.path.equals("/swagger.json")) {
        val accumulator: Accumulator[ByteString, Result] = next(requestHeader)
        accumulator.map(_.withHeaders(
          "Pragma" -> "no-cache"
        ))
      } else {
        next(requestHeader)
      }
    }
  }

}
