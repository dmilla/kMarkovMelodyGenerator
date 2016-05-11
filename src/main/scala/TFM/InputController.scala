package TFM

import akka.actor.Actor

/**
  * Created by diego on 9/05/16.
  */
class InputController extends Actor{



  def receive: Receive = {

    case _ ⇒ println("InputController received unknown message")
  }
}
