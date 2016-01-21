package util

import akka.actor.{Actor, ActorRef, Props}
import de.htwg.mps.hexxagon.controller.{BoardChanged, GameOver, HexxagonController}
import de.htwg.mps.hexxagon.model.Player
import play.api.libs.json.{JsBoolean, Json, JsValue}

import scala.swing.Reactor

object WebSocketActor {
  def props(controller: HexxagonController, out: ActorRef) = Props(new WebSocketActor(controller, out))
}

class WebSocketActor(controller: HexxagonController, out: ActorRef) extends Actor with Reactor {

  case class Field(x: String, y: String, f: String)

  implicit val fieldFormat = Json.format[Field]

  listenTo(controller)
  reactions += {
    case e: BoardChanged => out ! gameToJson
    case e: GameOver => out ! "over"
  }

  def receive = {
    case input: JsValue => {
      val x = (input \ "x").validate[Int].getOrElse(0)
      val y = (input \ "y").validate[Int].getOrElse(0)
      controller.input(x, y)
    }
  }

  def gameToJson = {
    def playerToJson(player: Player) = {
      val active = if (controller.currPlayerIndex == player.index) true else false
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
      "players" -> Json.arr(player2, player1)
    )
    gameState
  }

}