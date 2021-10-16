package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom.{console, document}

import pool.container.Container
import pool.dialog.{AccountDialog, LoginDialog, PoolDialog, RegisterDialog}
import pool.menu.HomeMenu
import pool.view.PoolsView

object Context {
  def apply(publicUrl: String, apiUrl: String): Context = new Context(publicUrl, apiUrl)

  def log(message: Any): Unit = console.log(message)
}

class Context(publicUrl: String, apiUrl: String) {
  log(s"[context] public url: $publicUrl api url: $apiUrl")

  val email = Var("")
  val pin = Var("")
  val account = Var(Account.emptyAccount)
  val pools = Var(Seq.empty[Pool])
  val pool = Var[Pool](Pool())

  val content = Container(
    HomeMenu(
      this,
      RegisterDialog(this),
      LoginDialog(this),
      AccountDialog(this)
    ),
    PoolsView(this, PoolDialog(this))
  )

  val registerUrl = s"$publicUrl/register"
  val loginUrl = s"$publicUrl/login"
  val deactivateUrl = s"$publicUrl/deactivate"
  val reactivateUrl = s"$publicUrl/reactivate"
  val poolsUrl = s"$apiUrl/pools"
  val poolsAddUrl = s"$apiUrl/pools/add"
  val poolsUpdateUrl = s"$apiUrl/pools/update"

  def log(message: Any): Unit = Context.log(message)

  def disable(id: String): Unit = setDisabled(id, "true")

  def enable(id: String): Unit = setDisabled(id, "false")

  def setDisabled(id: String, isDisabled: String): Unit = document.getElementById(id) match {
    case element: Element => element.setAttribute("disabled", isDisabled)
    case _ =>
  }

  def hide(id: String): Unit = document.getElementById(id).setAttribute("style", "display: none")

  def show(id: String): Unit = document.getElementById(id).setAttribute("style", "display: block")

  def toggle(id: String, style: String): Boolean = document.getElementById(id).classList.toggle(style)

  def add(id: String, style: String): Unit = document.getElementById(id).classList.add(style)

  def remove(id: String, style: String): Unit = document.getElementById(id).classList.remove(style)
}