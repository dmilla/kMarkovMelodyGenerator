package TFM

import TFM.CommProtocol.{CalcNoteOutputRequest, NotifyNoteFinished, TransitionsRequest, UpdateStatus}
import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by diego on 31/05/16.
  */


class Conductor extends Actor{

  var lastState: (Int, Int) = (0, 4)

  def sendNextNote = {
    val coords = getLastCoords
    implicit val timeout = Timeout(2 seconds)
    val transitions: Future[List[(Int, Double)]] = ask(kMMGUI.markovExtractor, TransitionsRequest(lastState)).mapTo[List[(Int, Double)]]
    transitions.onSuccess{
      case list: List[((Int, Int), Double)] =>
        if (list.nonEmpty) kMMGUI.controller ! CalcNoteOutputRequest(list, coords._1, coords._2)
        else notify("Error while trying to get Markov Transitions, please generate model first")
    }
  }

  def getLastCoords = {
    (JoystickChart.lastX, JoystickChart.lastY)
  }

  def notify(msg: String) = kMMGUI.addOutput(msg)

  def receive: Receive = {
    case NotifyNoteFinished => sendNextNote
    case UpdateStatus(status: (Int, Int)) => lastState = status
    case _ ⇒ println("Conductor received unknown message")
  }

}
