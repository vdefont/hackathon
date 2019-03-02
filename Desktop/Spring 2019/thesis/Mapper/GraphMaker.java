import javax.swing.*;
import java.util.List;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Util.Graph.*;
import Util.*;

class MyPanel extends JPanel {

  private static final int
    ROW_SPACING = 30,
    COL_SPACING = 50;

  private List<Point> vertices;
  private List<Edge<Point>> edges;
  private List<Cluster> clusters;
  private String curText;

  private Cluster closestCluster (int x, int y) {
    Cluster closest = null;
    double minDist = Double.MAX_VALUE;
    for (int i = 0; i < clusters.size(); i += 1) {
      List<Double> coords = vertices.get(i).getCoords();
      double dx = COL_SPACING * coords.get(0) - x;
      double dy = ROW_SPACING * coords.get(1) - y;
      double dist = Math.pow(dx*dx + dy*dy, 0.5);
      if (dist < minDist) {
        minDist = dist;
        closest = clusters.get(i);
      }
    }
    return closest;
  }

  public void paint (Graphics g) {
    // Draw vertices
    int numVertices = vertices.size();
    for (int i = 0; i < numVertices; i += 1) {
      Point p = vertices.get(i);
      int numPointsInCluster = clusters.get(i).numPoints();
      int rootNumPoints = (int) Math.pow(numPointsInCluster, 0.5);
      int width = rootNumPoints + 2;
      g.fillOval(COL_SPACING * p.getX() - width/2, ROW_SPACING * p.getY() - width/2, width, width);
    }

    // Draw edges
    for (Edge<Point> e : edges) {
      Point src = e.getSrc();
      Point dst = e.getDst();
      g.drawLine(COL_SPACING * src.getX(), ROW_SPACING * src.getY(),
        COL_SPACING * dst.getX(), ROW_SPACING * dst.getY());
    }

    g.drawString(curText, WIDTH/2, 10);
  }

  public MyPanel(List<Point> vertices, List<Edge<Point>> edges, List<Cluster> clusters){
    this.vertices = vertices;
    this.edges = edges;
    this.clusters = clusters;
    curText = "";

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        Cluster closest = closestCluster(e.getX(), e.getY());
        curText = closest.toString() + ": " + closest.printPoints();
        System.out.println(curText);
        repaint();
      }
    });
  }

}

public class GraphMaker extends JFrame {
    public GraphMaker (List<Point> vertices, List<Edge<Point>> edges, List<Cluster> clusters) {
      // Fill screen
      setSize(1200, 700);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      setContentPane(new MyPanel(vertices, edges, clusters));
      setVisible (true);
    }
}
