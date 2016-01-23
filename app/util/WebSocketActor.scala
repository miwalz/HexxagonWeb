package util

import akka.actor.{Actor, ActorRef, Props}
import de.htwg.mps.hexxagon.controller.HexxagonController
import de.htwg.mps.hexxagon.model.Player
import de.htwg.mps.hexxagon.util.{GameOver, BoardChanged}
import play.api.libs.json._
import play.api.libs.json.{Json, JsValue}

import scala.swing.Reactor

object WebSocketActor {
  def props(controller: HexxagonController, out: ActorRef) = Props(new WebSocketActor(controller, out))
}

class WebSocketActor(controller: HexxagonController, out: ActorRef) extends Actor with Reactor {

  case class Field(x: Int, y: Int, f: String)

  implicit val fieldFormat = Json.format[Field]

  listenTo(controller)
  reactions += {
    case e: BoardChanged => out ! gameToJson(false)
    case e: GameOver => out ! gameToJson(true)
  }

  def receive = {
    case input: JsValue => input match {
      case JsString("init") => controller.init(controller.p1.name, controller.p2.name)
      case _ =>
        val x = (input \ "x").validate[Int].getOrElse(0)
        val y = (input \ "y").validate[Int].getOrElse(0)
        controller.input(x, y)
    }
  }

  def gameToJson(gameover: Boolean) = {
    def playerToJson(player: Player) = {
      val active = controller.currPlayerIndex == player.index
      Json.obj(
        "name" -> player.name,
        "score" -> player.score,
        "active" -> JsBoolean(active)
      )
    }
    val fieldList = controller.getFieldsAsList.map(f => Field(f._1, f._2, f._3))
    val player1 = playerToJson(controller.p1)
    val player2 = playerToJson(controller.p2)
    val gameState = Json.obj(
      "fields" -> fieldList,
      "players" -> Json.arr(player1, player2),
      "gameOver" -> JsBoolean(gameover)
    )
    gameState
  }

}