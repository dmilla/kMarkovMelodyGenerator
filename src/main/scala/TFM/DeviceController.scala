package TFM

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.io.IO
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.util.ByteString
import com.github.jodersky.flow.{Parity, SerialSettings}
import com.github.jodersky.flow.stream._

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by diego on 9/05/16.
  */
class DeviceController extends Actor{

  //TODO - INSTANTIATE DEVICE CONTROLLER, WATCH NEW PORTS AND TRY TO CONNECT TO DEVICE AUTOMATICALLY
  val BAUD_RATE = 115200
  val CHARACTER_SIZE = 8
  val PARITY = Parity.None
  val TWO_STOP_BITS = false
  val DEVICE_SETTINGS = SerialSettings(BAUD_RATE, CHARACTER_SIZE, TWO_STOP_BITS, PARITY)
  //constexpr static const size_t READ_BUFFER_SIZE = sizeof(ReadBuffer);
  //constexpr static const size_t WRITE_BUFFER_SIZE = sizeof(WriteBuffer);
  val READ_BUFFER_SIZE = 4
  val WRITE_BUFFER_SIZE = 4
  val SENSOR_STEPS = 4096
  val DEGREES_PER_STEP = SENSOR_STEPS / 360
  val DISTANCE_FROM_CENTER_TO_MOTORS = 3.0
  val ARM_LENGTH_1 = 27.0
  val ARM_LENGTH_2 = 30.0
  val PORT = "/dev/ttyUSB0"

  val Delay = FiniteDuration(500, MILLISECONDS)

  val serial: Flow[ByteString, ByteString, Future[Serial.Connection]] =
    Serial().open(PORT, DEVICE_SETTINGS)

  val printer: Sink[ByteString, _] = Sink.foreach[ByteString]{data =>
    notify("server says: " + data.decodeString("UTF-8"))
  }

  val ticker: Source[ByteString, _] = Source.tick(Delay, Delay, ()).scan(0){case (x, _) =>
    x + 1
  }.map{ x =>
    notify(x.toString)
    ByteString(x.toString)
  }

  val connection: Future[Serial.Connection] = ticker.viaMat(serial)(Keep.right).to(printer).run()

  def notify(msg: String) = kMMGUI.addOutput(msg)

  def receive: Receive = {
    case _ ⇒ println("InputController received unknown message")
  }
}
