package TFM

import javax.sound.midi.{MidiDevice, MidiSystem, ShortMessage}

import TFM.CommProtocol.{NotifyNoteFinished, SendMidiNoteRequest}
import akka.actor.Actor

/**
  * Created by diego on 7/05/16.
  */
class MidiSender extends Actor{

  val rcvr = MidiSystem.getReceiver
  var beatsPerMinute = 120 // TODO variable tempo add GUI Fields
  var semiQuaversPerSec: Float = (beatsPerMinute * 4.0f)/60.0f

  def sendNote(note: (Int, Int)) = {
    val onMsg = new ShortMessage()
    onMsg.setMessage(ShortMessage.NOTE_ON, 0, note._1, 93)
    val offMsg = new ShortMessage()
    offMsg.setMessage(ShortMessage.NOTE_OFF, 0, note._1, 93)
    //notifyMidiDevices()
    rcvr.send(onMsg, -1)
    Thread.sleep((1000 * (note._2/semiQuaversPerSec)).round) //TODO improve implementation of NOTE_OFF
    kMMGUI.conductor ! NotifyNoteFinished
    rcvr.send(offMsg, -1)
    notify("\n" + note + " midi note sent!\n")
  }

  def notifyMidiDevices() = {
    val devices = MidiSystem.getMidiDeviceInfo
    devices.foreach( info =>
      notify("    Name: " + info.toString +
        ", Decription: " +
        info.getDescription +
        ", Vendor: " +
        info.getVendor)
    )
  }

  def notify(msg: String) = kMMGUI.addOutput(msg)

  def receive = {
    case SendMidiNoteRequest(note: (Int, Int)) => sendNote(note)
    case _ ⇒ println("FeaturesExtractor received unknown message")
  }

}
