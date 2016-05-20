package TFM

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.io.IO
import akka.util.ByteString
import com.github.jodersky.flow.{Serial, SerialSettings}

/**
  * Created by diego on 9/05/16.
  */
class DeviceController extends Actor{

  //constexpr static const size_t READ_BUFFER_SIZE = sizeof(ReadBuffer);
  //constexpr static const size_t WRITE_BUFFER_SIZE = sizeof(WriteBuffer);
  val READ_BUFFER_SIZE = 4
  val WRITE_BUFFER_SIZE = 4
  val SENSOR_STEPS = 4096
  val DEGREES_PER_STEP = SENSOR_STEPS / 360
  val DISTANCE_FROM_CENTER_TO_MOTORS = 3.0
  val ARM_LENGTH_1 = 27.0
  val ARM_LENGTH_2 = 30.0

  def receive: Receive = {
    case _ ⇒ println("InputController received unknown message")
  }
}
