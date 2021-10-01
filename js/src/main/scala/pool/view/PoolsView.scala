package pool.view

import com.raquo.laminar.api.L._

import pool._
import pool.container._
import pool.dialog.{Add, Edit, PoolDialog, View}
import pool.handler.StateHandler
import pool.menu.{MenuButton, MenuButtonBar}
import pool.proxy.EntityProxy
import pool.text.{Errors, Header}

object PoolsView {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]
  var listItems = Var(Seq.empty[Li]).toObservable

  def handler(context: Context, errors: EventBus[String], state: State): Unit =
    state match {
      case pools: Pools =>
        context.pools.set(pools.pools)
        listItems = context.pools.signal.split(_.id)((_, _, pool) => ListView.renderItem(pool.map(_.name)))
      case id: Id => context.log(s"Pool id: $id for add pool.")
      case count: Count => context.log(s"Pool count: $count for update pool.")
      case _ => errors.emit(s"Invalid: $state")
    }

  def postApply(context: Context): Unit = {
    val license = License(context.account.now().license)
    val response = EntityProxy.post(context.poolsUrl, license.key, license)
    StateHandler.handle(context, errors, response, handler)
  }

  def apply(context: Context): Div =
    Container(id = id, isDisplayed = "none",
      Header("Pools"),
      Errors(errors),
      ListView(listItems),
      MenuButtonBar(
        MenuButton(name = "Add").amend {
          onClick --> { _ =>
            context.container.amend {
              PoolDialog(Add, context, context.account, Var(Pool.emptyPool))
            }
          }
        },
        MenuButton(name = "Edit").amend {
          onClick --> { _ =>
            context.container.amend {
              PoolDialog(Edit, context, context.account, context.pool)
            }
          }
        },
        MenuButton(name = "View").amend {
          onClick --> { _ =>
            context.container.amend {
              PoolDialog(View, context, context.account, context.pool, readOnly = true)
            }
          }
        }
      )
    )
}