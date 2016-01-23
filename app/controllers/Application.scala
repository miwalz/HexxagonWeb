package controllers

import de.htwg.mps.hexxagon.controller.HexxagonController
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._
import util.WebSocketActor
import play.api.Play.current

import scala.swing.Reactor
import scala.util.control.NonFatal

class Application extends Controller with Reactor {

  implicit val jsonFrame: FrameFormatter[JsValue] =
    implicitly[FrameFormatter[String]].transform(Json.stringify, { text =>
      try {
        Json.parse(text)
      } catch {
        case NonFatal(e) => Json.obj("error" -> e.getMessage)
      }
    })

  val controller = HexxagonController()

  def index = Action {
    Ok(views.html.index())
  }

  def gameNew(p1: String, p2: String) = Action {
    controller.init(p1, p2)
    Ok(views.html.board())
  }

  def socket = WebSocket.acceptWithActor[JsValue, JsValue] {request => out =>
    WebSocketActor.props(controller, out)
  }

}