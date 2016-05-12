package TFM

import java.awt.Dimension
import java.text.DecimalFormat

import scala.swing._

/**
  * Created by diego on 12/05/16.
  */
class OctaveProbsFields extends BoxPanel(Orientation.Horizontal){

  val space = 3
  val longSpace = 10
  val fieldSize = new Dimension(50, 25)
  val labelSize = new Dimension(30, 25)
  val emptyBorder = new javax.swing.border.EmptyBorder(2, 2, 2, 2)

  val formatter = new DecimalFormat("#.##")

  val label0 = new Label("0:")
  label0.peer.setMaximumSize(labelSize)
  label0.horizontalAlignment = Alignment.Right
  contents += label0
  contents += Swing.HStrut(space)
  val probField0 = new TextField
  probField0.editable = false
  probField0.peer.setMaximumSize(fieldSize)
  probField0.border = emptyBorder
  //probField0.peer.setForeground(new java.awt.Color(0, 153, 51)) TODO SET COLOR GREEN IF PROB INCREASED
  contents += probField0

  contents += Swing.HStrut(longSpace)

  val label1 = new Label("1:")
  label1.peer.setMaximumSize(labelSize)
  label1.horizontalAlignment = Alignment.Right
  contents += label1
  contents += Swing.HStrut(space)
  val probField1 = new TextField()
  probField1.editable = false
  probField1.peer.setMaximumSize(fieldSize)
  probField1.border = emptyBorder
  contents += probField1

  contents += Swing.HStrut(longSpace)

  val label2 = new Label("2:")
  label2.peer.setMaximumSize(labelSize)
  label2.horizontalAlignment = Alignment.Right
  contents += label2
  contents += Swing.HStrut(space)
  val probField2 = new TextField()
  probField2.editable = false
  probField2.peer.setMaximumSize(fieldSize)
  probField2.border = emptyBorder
  contents += probField2

  contents += Swing.HStrut(longSpace)

  val label3 = new Label("3:")
  label3.peer.setMaximumSize(labelSize)
  label3.horizontalAlignment = Alignment.Right
  contents += label3
  contents += Swing.HStrut(space)
  val probField3 = new TextField()
  probField3.editable = false
  probField3.peer.setMaximumSize(fieldSize)
  probField3.border = emptyBorder
  contents += probField3

  contents += Swing.HStrut(longSpace)

  val label4 = new Label("4:")
  label4.peer.setMaximumSize(labelSize)
  label4.horizontalAlignment = Alignment.Right
  contents += label4
  contents += Swing.HStrut(space)
  val probField4 = new TextField()
  probField4.editable = false
  probField4.peer.setMaximumSize(fieldSize)
  probField4.border = emptyBorder
  contents += probField4

  contents += Swing.HStrut(longSpace)

  val label5 = new Label("5:")
  label5.peer.setMaximumSize(labelSize)
  label5.horizontalAlignment = Alignment.Right
  contents += label5
  contents += Swing.HStrut(space)
  val probField5 = new TextField()
  probField5.editable = false
  probField5.peer.setMaximumSize(fieldSize)
  probField5.border = emptyBorder
  contents += probField5

  contents += Swing.HStrut(longSpace)

  val label6 = new Label("6:")
  label6.peer.setMaximumSize(labelSize)
  label6.horizontalAlignment = Alignment.Right
  contents += label6
  contents += Swing.HStrut(space)
  val probField6 = new TextField()
  probField6.editable = false
  probField6.peer.setMaximumSize(fieldSize)
  probField6.border = emptyBorder
  contents += probField6

  contents += Swing.HStrut(longSpace)

  val label7 = new Label("7:")
  label7.peer.setMaximumSize(labelSize)
  label7.horizontalAlignment = Alignment.Right
  contents += label7
  contents += Swing.HStrut(space)
  val probField7 = new TextField()
  probField7.editable = false
  probField7.peer.setMaximumSize(fieldSize)
  probField7.border = emptyBorder
  contents += probField7

  contents += Swing.HStrut(longSpace)

  val label8 = new Label("8:")
  label8.peer.setMaximumSize(labelSize)
  label8.horizontalAlignment = Alignment.Right
  contents += label8
  contents += Swing.HStrut(space)
  val probField8 = new TextField()
  probField8.editable = false
  probField8.peer.setMaximumSize(fieldSize)
  probField8.border = emptyBorder
  contents += probField8

  contents += Swing.HStrut(longSpace)

  val label9 = new Label("9:")
  label9.peer.setMaximumSize(labelSize)
  label9.horizontalAlignment = Alignment.Right
  contents += label9
  contents += Swing.HStrut(space)
  val probField9 = new TextField()
  probField9.editable = false
  probField9.peer.setMaximumSize(fieldSize)
  probField9.border = emptyBorder
  contents += probField9

  contents += Swing.HStrut(longSpace)

  val label10 = new Label("10:")
  label10.peer.setMaximumSize(labelSize)
  label10.horizontalAlignment = Alignment.Right
  contents += label10
  contents += Swing.HStrut(space)
  val probField10 = new TextField()
  probField10.editable = false
  probField10.peer.setMaximumSize(fieldSize)
  probField10.border = emptyBorder
  contents += probField10

  contents += Swing.HStrut(longSpace)

  val label11 = new Label("11:")
  label11.peer.setMaximumSize(labelSize)
  label11.horizontalAlignment = Alignment.Right
  contents += label11
  contents += Swing.HStrut(space)
  val probField11 = new TextField()
  probField11.editable = false
  probField11.peer.setMaximumSize(fieldSize)
  probField11.border = emptyBorder
  contents += probField11

  def fillFromMap(map: Map[Int, Double]) = {
    probField0.text = doubleToProbString(map(0))
    probField1.text = doubleToProbString(map(1))
    probField2.text = doubleToProbString(map(2))
    probField3.text = doubleToProbString(map(3))
    probField4.text = doubleToProbString(map(4))
    probField5.text = doubleToProbString(map(5))
    probField6.text = doubleToProbString(map(6))
    probField7.text = doubleToProbString(map(7))
    probField8.text = doubleToProbString(map(8))
    probField9.text = doubleToProbString(map(9))
    probField10.text = doubleToProbString(map(10))
    probField11.text = doubleToProbString(map(11))
  }

  def doubleToProbString(d: Double) = {
    formatter.format(d * 100) + "%"
  }
}
