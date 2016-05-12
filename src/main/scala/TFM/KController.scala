package TFM

import java.text.DecimalFormat

import TFM.CommProtocol.{CalcNoteOutputRequest, SendMidiNoteRequest}
import akka.actor.Actor

/**
  * Created by diego on 9/05/16.
  */
class KController(ui: UI) extends Actor{

  var affectedNotes = 1
  var k: Double = 0.5

  val formatter = new DecimalFormat("#.##")

  def calcNoteOutput(markovProbabilites: List[(Int, Double)], yPosition: Double) = {
    val size = markovProbabilites.size
    val controlNote = ((size - 1) * yPosition).round.toInt
    //notify("Initial Markov probabilites: " + markovProbabilites)
    notify("Control Note is: " + controlNote)
    val controlProbabilities = calcControlProbabilities(markovProbabilites: List[(Int, Double)], yPosition: Double, controlNote: Int).toMap
    //notify("Final output probabilities: " + controlProbabilities)
    val out = sample[Int](controlProbabilities)
    notify("Nota de salida: " + out + " - Se reproducirá una nota normalizada 48 notas por encima")
    ui.midiSender ! SendMidiNoteRequest(out + 48) //TODO normalize output as well
    updateControlProbs(controlProbabilities, yPosition)
    kMMGUI.updateState(out)
  }

  def calcControlProbabilities(markovProbabilites: List[(Int, Double)], yPosition: Double, controlNote: Int) = {
    val probs = scala.collection.mutable.Map[Int, Double]()
    markovProbabilites.foreach{
      case(note, prob) =>
        probs += (note -> calcNoteProbability(prob, note, controlNote))
    }
    probs
  }

  def calcNoteProbability(markovProbability: Double, note: Int, controlNote: Int): Double = {
    val distance: Int = math.abs(controlNote - note)
    var outProb: Double = k * markovProbability
    if (distance <= affectedNotes) {
      val increase: Double = (1.0 - k) * (1.0/((affectedNotes * 2.0) + 1.0))
      notify(increase * 100 + "% prob increase for note " + note)
      outProb += increase
    }
    outProb
  }

  final def sample[A](dist: Map[A, Double]): A = {
    val p = scala.util.Random.nextDouble * dist.values.sum
    val it = dist.iterator
    var accum = 0.0
    while (it.hasNext) {
      val (item, itemProb) = it.next
      accum += itemProb
      if (accum >= p)
        return item  // return so that we don't have to search through the whole distribution
    }
    sys.error(f"this should never happen")  // needed so it will compile
  }

  def updateAffectedNotes(newValue: Int) ={
    affectedNotes = newValue
  }

  def updateK(newValue: Double) ={
    k = newValue
  }

  def notify(msg: String) = kMMGUI.addOutput(msg)

  def updateControlProbs(probs: Map[Int, Double], yPosition: Double) = {
    kMMGUI.controlOctave1.fillFromMap(probs)
    kMMGUI.controlOctaveLabel.text = "Probabilidades de las notas modificadas por el control con la posición Y " + formatter.format(yPosition) + ":"
  }

  def receive: Receive = {
    case CalcNoteOutputRequest(markovProbabilities, yPosition) => calcNoteOutput(markovProbabilities, yPosition)
    case _ ⇒ println("InputController received unknown message")
  }
}
