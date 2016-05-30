package TFM

/**
  * Created by diego on 30/05/16.
  */

import java.awt.Color
import javax.swing.{BorderFactory, JButton, JFrame, JPanel}

import scala.swing._
import org.jfree.chart._
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYItemRenderer
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection

object JoystickChart extends JFrame{

  setTitle("Joystick Position")
  setSize(new Dimension(700, 450))
  val parentPanel = new JPanel()
  val chartPanel = createChartPanel
  chartPanel.setDomainZoomable(false)
  chartPanel.setRangeZoomable(false)

  chartPanel.addChartMouseListener(new ChartMouseListener() {

    def chartMouseClicked(event: ChartMouseEvent ){
      getMouseCoords(event)
    }

    def chartMouseMoved(event: ChartMouseEvent ){
      //getMouseCoords(event)
    }
  })

  parentPanel.add(chartPanel)
  add(parentPanel)
  //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  //frame.setVisible(true)

  def getMouseCoords(mouseChartEvent: ChartMouseEvent) = {
    val plot = chartPanel.getChart().getXYPlot()
    val p = chartPanel.translateScreenToJava2D(mouseChartEvent.getTrigger().getPoint())
    val plotArea = chartPanel.getScreenDataArea()
    val chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge())
    val chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge())
    //val coords = (chartX, chartY)
    plot.setDataset(createDatasetFromPoint(chartX, chartY))
  }

  def createChartPanel: ChartPanel = {
    val jfreechart = ChartFactory.createScatterPlot("Joystick Position", "X", "Y", createDatasetFromPoint(0.5f, 0.5f), PlotOrientation.VERTICAL, true, true, false)
    val xyPlot = jfreechart.getXYPlot()
    xyPlot.setDomainCrosshairVisible(true)
    xyPlot.setRangeCrosshairVisible(true)
    val renderer = xyPlot.getRenderer();
    renderer.setSeriesPaint(0, Color.blue);
    val domain = xyPlot.getDomainAxis()
    domain.setRange(0.00, 1.00)
    domain.setVerticalTickLabels(true)
    val range = xyPlot.getRangeAxis()
    range.setRange(0.0, 1.0);
    //range.setTickUnit(new NumberTickUnit(0.1));
    new ChartPanel(jfreechart)
  }

  def refreshChart(chart: JFreeChart, x: Double, y: Double) = chart.getXYPlot().setDataset(createDatasetFromPoint(x, y))

  def createDatasetFromPoint(x: Double, y: Double): XYDataset = {
    var drawX = x
    var drawY = y
    if (x > 1) drawX = 1
    if (x < 0) drawX = 0
    if (y > 1) drawY = 1
    if (y < 0) drawY = 0
    val xySeriesCollection = new XYSeriesCollection()
    val series = new XYSeries("Current Position")
    series.add(x, y)
    xySeriesCollection.addSeries(series)
    xySeriesCollection
  }

}
