package TFM

import java.io.File

import TFM.CommProtocol.{HMMExtractionRequest, TransitionsRequest, UpdateMarkovProbsRequest}
import akka.actor.Actor

import scala.collection.mutable.ArrayBuffer

/**
  * Created by diego on 6/05/16.
  */
class MarkovExtractor extends Actor{

  var octaves = 1
  var markovChain = new MarkovChain[Int]()

  def extractMarkovChain(path: String) = {
    notify("Calculando Markov Chain con las notas de los archivos txt en la carpeta: " + path)
    val pathFile = new File(path)
    val notes = ArrayBuffer.empty[Int]
    var count = 0
    for(file <- pathFile.listFiles if file.getName endsWith ".txt"){
      try {
        val file_notes = extractNotesFromTxt(file)
        notify("Extraidas " + file_notes.size + " notas de " + file.getName)
        notes ++= file_notes
        count += 1
      } catch {
        case e: Exception => notify("Excepción extrayendo notas del archivo " + file.getName + " en " + path + " : " + e);
      }
    }
    val normalizedNotes = normalizeNotes(notes, octaves)
    initializeMarkovChain
    var prevNote = 999
    normalizedNotes.foreach( note =>
      if (prevNote == 999) {
        prevNote = note
      } else {
        markovChain = markovChain.addTransition(prevNote, note)
        prevNote = note
      }
    )
    notify(markovChain.states().toString())
    markovChain.states().foreach( state =>
      notify("transitions for " + state + ": " + markovChain.transitionsFor(state).toString())
    )
    updateMarkovProbs(kMMGUI.lastNoteField.text.toInt)
    notify("\n¡Notas extraídas exitosamente de los " + count + " ficheros encontrados en " + path + "! Se ha generado un modelo de Markov con las transiciones")
  }

  def extractNotesFromTxt(file: File) = {
    val source = scala.io.Source.fromFile(file)
    val file_notes = try source.mkString.split(", ") finally source.close()
    file_notes.map(_.toInt)
  }

  def normalizeNotes(notes: ArrayBuffer[Int], octaves: Int) = {
    val normalizedNotes = ArrayBuffer.empty[Int]
    for (note <- notes) {
      normalizedNotes += normalizeNote(note, octaves)
    }
    normalizedNotes.toVector
  }

  // TODO: GENERALIZE TO MORE THAN 1 OCTAVE
  def normalizeNote(note: Int, octaves: Int) = {
    /*var normalizedNote = note
    val above = note/(octaves*12)
    note match {
      case x if x < 12 => normalizedNote = note
      case x if x < 24 => normalizedNote = note
      case x if x < 36 => octave0 += 1
      case x if x < 48 => octave1 += 1
      case x if x < 60 => octave2 += 1
      case x if x < 72 => octave3 += 1
      case x if x < 84 => octave4 += 1
      case x if x < 96 => octave5 += 1
      case x if x < 108 => octave6 += 1
      case x if x < 120 => octave7 += 1
      case x if x < 128 => octave8 += 1
    }*/
    note - (note/(octaves * 12)) * 12 //Only works for 1 octave
  }

  def initializeMarkovChain = {
    val maxNote: Int = (octaves * 12)
    val notes = List.range(0, maxNote)
    notes.foreach( startNote =>
      notes.foreach( endNote =>
        markovChain = markovChain.addTransition(startNote, endNote)
      )
    )
  }

  def notify(msg: String) = kMMGUI.addOutput(msg)

  def updateMarkovProbs(state: Int) = {
    kMMGUI.markovOctave1.fillFromMap(markovChain.transitionsFor(normalizeNote(state, octaves)).toMap)
    kMMGUI.markovOctaveLabel.text = "Probabilidades de las notas para el estado actual (" + state + ") :"
  }

  def receive = {
    case UpdateMarkovProbsRequest(state: Int) => updateMarkovProbs(state)
    case TransitionsRequest(state: Int) => sender ! markovChain.transitionsFor(normalizeNote(state, octaves))
    case HMMExtractionRequest(path) => extractMarkovChain(path)
    case _ ⇒ println("FeaturesExtractor received unknown message")
  }

}
