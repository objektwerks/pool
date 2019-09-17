package tripletail

import org.scalajs.dom.console
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class PoolServerClient(serverUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def post(path: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    import Serialization._
    import upickle.default._

    val headersWithLicense = headers + (Licensee.licenseHeaderKey -> license)
    Ajax.post(url = serverUrl + path, headers = headersWithLicense, data = write(entity)).map { xhr =>
      xhr.status match {
        case 200 => Try(read[State](xhr.responseText)).fold(error => Left(Fault(error)), state => Right(state))
        case _ => Left( log(Fault(xhr.statusText, xhr.status)) )
      }
    }.recover { case error => Left( log(Fault(error)) ) }
  }

  def log(fault: Fault): Fault = {
    console.error(fault.toString)
    fault
  }
}

object PoolServerClient {
  def apply(serverUrl: String): PoolServerClient = new PoolServerClient(serverUrl)
}