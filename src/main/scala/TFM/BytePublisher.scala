package TFM

import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import akka.util.ByteString

import scala.collection.mutable

/**
  * Created by root on 27/05/16.
  */
class BytePublisher extends ActorPublisher[ByteString] {
  var queue: mutable.Queue[ByteString] = mutable.Queue()

  def receive = {
    case Publish(b: ByteString) =>
      if (queue.isEmpty) queue.enqueue(b)
      else notify("BytePublisher received another request but queue is already full")
      publishIfNeeded()
    case Request(cnt) =>
      publishIfNeeded()
    case Cancel => context.stop(self)
    case _ =>
  }

  def publishIfNeeded() = {
    while (queue.nonEmpty && isActive && totalDemand > 0) {
      onNext(queue.dequeue())
    }
  }

  def notify(msg: String) = kMMGUI.addOutput(msg)
}

case class Publish[D](data: D)