package pool.proxy

import org.scalajs.dom.ext.Ajax

import pool.{Command, Context, Event, Fault, Serializers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object CommandProxy extends Proxy {
  import Serializers._
  import upickle.default._

  def post(url: String, license: String, command: Command): Future[Either[Fault, Event]] = {
    Context.log(s"Command: $command")
    Ajax.post(url = url, headers = headers(license), data = write[Command](command)).map { xhr =>
      xhr.status match {
        case 200 => Try(read[Event](xhr.responseText)).fold(error => Left(log(error)), event => Right(event))
        case 400 | 500 => Left( readAsFault(xhr.responseText) )
        case _ => Left( log(xhr.statusText) )
      }
    }.recover { case error => Left( log(error) ) }
  }
}