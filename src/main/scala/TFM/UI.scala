package TFM

/**
  * Created by diego on 05/05/16.
  */
import java.io.File
import java.text.NumberFormat

import TFM.CommProtocol._
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.swing._


class UI extends MainFrame {

  title = "kMarkovMelodyGenerator - Diego Milla - TFM - Máster SSII - USAL"
  preferredSize = new Dimension(1500, 1000)
  val actorSystem = ActorSystem("TFMSystem")
  val watcher = actorSystem.actorOf(Props(classOf[DeviceWatcher]), name = "deviceWatcher")
  val deviceController = actorSystem.actorOf(Props(classOf[DeviceController]))
  val markovExtractor = actorSystem.actorOf(Props(classOf[MarkovExtractor]))
  val midiSender = actorSystem.actorOf(Props[MidiSender])
  val controller = actorSystem.actorOf(Props(classOf[KController], this))
  val textFieldSize = new Dimension(300, 25)
  val labelSize = new Dimension(300, 25)
  val numberFieldSize = new Dimension(60, 25)
  val noteField = new TextField { text = "48" }
  noteField.peer.setMaximumSize(numberFieldSize)
  val lastNoteField = new TextField { text = "0" }
  lastNoteField.peer.setMaximumSize(numberFieldSize)
  val yPosField = new TextField { text = "0.8" }
  yPosField.peer.setMaximumSize(numberFieldSize)
  val outputField = new TextArea { rows = 26; lineWrap = true; wordWrap = true; editable = false }
  //val extractorOutputField = new TextArea { rows = 12; lineWrap = true; wordWrap = true; editable = false }
  val defaultPathFile = new File(System.getProperty("user.home") + "/MidiWebMiner/notes/Piano") // TODO inicializar directorio en carpeta general
  //defaultPathFile.mkdirs
  val notesDirChooser = new FileChooser(defaultPathFile)
  val notesDirField = new TextField( defaultPathFile.getAbsolutePath )
  notesDirChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
  notesDirChooser.title = "Selecciona el directorio con las secuencias de notas objetivo MIDIs"
  notesDirField.peer.setMaximumSize(textFieldSize)
  notesDirField.editable = false

  val markovOctaveLabel = new Label("Probabilidades de las notas para el estado actual:")
  val markovOctave1 = new OctaveProbsFields
  val controlOctaveLabel = new Label("Probabilidades de las notas modificadas por el control:")
  val controlOctave1 = new OctaveProbsFields

  //TODO - USE FormattedTextFields to avoid bad entries in number fields
  val integerFieldFormatter = NumberFormat.getIntegerInstance()

  val portField = new TextField { text = "/dev/ttyUSB0" }
  portField.peer.setMaximumSize(textFieldSize)

  contents = new BoxPanel(Orientation.Vertical) {

    contents += new BoxPanel(Orientation.Horizontal) {
      val label = new Label("Directorio de las secuencias de notas")
      label.peer.setMaximumSize(labelSize)
      label.horizontalAlignment = Alignment.Left
      contents += label
      contents += Swing.HStrut(5)
      contents += notesDirField
      contents += Button("Seleccionar") {
        val res = notesDirChooser.showOpenDialog(this)
        if (res == FileChooser.Result.Approve) {
          notesDirField.text = notesDirChooser.selectedFile.getPath
        } else None
      }
      contents += Swing.HStrut(5)
      contents += Button("Generar Modelo Markov") {
        markovExtractor ! HMMExtractionRequest(notesDirField.text)
      }
    }
    contents += Swing.VStrut(10)
    contents += new BoxPanel(Orientation.Horizontal) {
      val label = new Label("Enviar Nota Midi")
      label.peer.setMaximumSize(labelSize)
      label.horizontalAlignment = Alignment.Left
      contents += label
      contents += Swing.HStrut(5)
      contents += noteField
      contents += Swing.HStrut(5)
      contents += Button("Enviar") {
        midiSender ! SendMidiNoteRequest(noteField.text.toInt)
      }
    }
    contents += Swing.VStrut(10)
    contents += new BoxPanel(Orientation.Horizontal) {
      val label = new Label("Puerto del joystick")
      label.peer.setMaximumSize(labelSize)
      label.horizontalAlignment = Alignment.Left
      contents += label
      contents += Swing.HStrut(5)
      contents += portField
      contents += Swing.HStrut(5)
      contents += Button("Conectar") {
        deviceController ! ConnectToDeviceRequest(portField.text)
      }
    }
    contents += Swing.VStrut(10)
    contents += new BoxPanel(Orientation.Horizontal) {
      val label = new Label("Estado del modelo de Markov")
      label.peer.setMaximumSize(labelSize)
      label.horizontalAlignment = Alignment.Left
      contents += label
      contents += Swing.HStrut(5)
      contents += lastNoteField
      contents += Swing.HStrut(5)
      contents += Button("Actualizar Estado") {
        markovExtractor ! UpdateMarkovProbsRequest(lastNoteField.text.toInt)
      }
    }
    contents += markovOctaveLabel
    contents += markovOctave1
    contents += new BoxPanel(Orientation.Horizontal) {
      val label = new Label("Posición Y del joystick ([0, 1])")
      label.peer.setMaximumSize(labelSize)
      label.horizontalAlignment = Alignment.Left
      contents += label
      contents += Swing.HStrut(5)
      contents += yPosField
      contents += Swing.HStrut(5)
      contents += Button("Simular Control") {
        implicit val timeout = Timeout(2 seconds)
        val transitions: Future[List[(Int, Double)]] = ask(markovExtractor, TransitionsRequest(lastNoteField.text.toInt)).mapTo[List[(Int, Double)]]
        transitions.onSuccess{
          case list: List[(Int, Double)] =>
            if (list.nonEmpty) controller ! CalcNoteOutputRequest(list, yPosField.text.toDouble)
            else addOutput("Error while trying to get Markov Transitions, please generate model first")
        }
      }
    }
    contents += controlOctaveLabel
    contents += controlOctave1
    contents += Swing.VStrut(10)
    contents += new Label("Información")
    contents += Swing.VStrut(3)
    contents += new ScrollPane(outputField)
    contents += Swing.VStrut(10)
    for (e <- contents)
      e.xLayoutAlignment = 0.0
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }

  def updateState(state: Int) = {
    lastNoteField.text = state.toString
    markovExtractor ! UpdateMarkovProbsRequest(state)
  }

  def addOutput(out: String): Unit = {
    outputField.append(out + "\n")
    outputField.peer.setCaretPosition(outputField.peer.getDocument.getLength)
  }


}



