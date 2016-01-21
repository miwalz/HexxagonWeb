package controllers

import de.htwg.mps.hexxagon.controller.HexxagonController
import play.api.Play.current
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._
import util.WebSocketActor

import scala.swing.Reactor
import scala.util.control.NonFatal

class Hexxagon extends Controller with Reactor {

  implicit val myJsonFrame: FrameFormatter[JsValue] = implicitly[FrameFormatter[String]].transform(Json.stringify, { text =>
    try {
      Json.parse(text)
    } catch {
      case NonFatal(e) => Json.obj("error" -> e.getMessage)
    }
  })

  val hexxagon = HexxagonController()

  def gameNew(p1: String, p2: String) = Action {
    hexxagon.init(p1, p2)
    Ok(views.html.hexxagon(hexxagon))
  }

  def gameInput(x: Int, y: Int) = Action {
    hexxagon.input(x, y)
    Ok(views.html.board(hexxagon))
  }



  def test = Action {
    Ok(views.html.test())
  }

  def socket() = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    WebSocketActor.props(hexxagon, out)
  }



}