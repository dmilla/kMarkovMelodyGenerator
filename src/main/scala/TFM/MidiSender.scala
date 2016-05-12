package TFM

import javax.sound.midi.{MidiSystem, ShortMessage}

import TFM.CommProtocol.SendMidiNoteRequest
import akka.actor.Actor

/**
  * Created by diego on 7/05/16.
  */
class MidiSender extends Actor{

  val rcvr = MidiSystem.getReceiver

  def sendNote(note: Int) = {
    val myMsg = new ShortMessage()
    myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 93)
    //notifyMidiDevices()
    rcvr.send(myMsg, -1)
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
    case SendMidiNoteRequest(note) => sendNote(note)
    case _ ⇒ println("FeaturesExtractor received unknown message")
  }

}
