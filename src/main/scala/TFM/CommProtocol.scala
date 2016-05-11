package TFM

import java.io.File

/**
  * Created by diego on 30/03/16.
  */
object CommProtocol {

  case class CrawlRequest(url: String, followIf: String, depth: Int, downloadsDirectory: String)
  case class NotesExtractionRequest(midiFile: File)
  case class FolderNotesExtractionRequest(path: String)
  case class FeaturesExtractionRequest(path: String)
  case class HMMExtractionRequest(path: String)
  case class SendMidiNoteRequest(note: Int)
  case class CalcNoteOutputRequest(markovProbabilities: List[(Int, Double)], yPosition: Double)
  case class TransitionsRequest(state: Int)

}
