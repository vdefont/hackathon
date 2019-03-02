package Util.Graph;

import java.util.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class Cluster implements Comparable<Cluster> {

  private Interval interval;
  private int index;
  private Set<Point> points;

  public Cluster (Interval interval, int index) {
    this.interval = interval;
    this.index = index;
    points = new HashSet<>();
  }

  public String getAveragePoint () {
    List<Double> coords = new ArrayList<>();
    for (Point p : points) {
      List<Double> c = p.getCoords();
      if (coords.size() == 0) coords = c;
      else {
        for (int i = 0; i < coords.size(); i += 1) {
          double newD = coords.get(i) + c.get(i);
          coords.set(i, newD);
        }
      }
    }
    // Take average
    String s = "";
    for (int i = 0; i < coords.size(); i += 1) {
      double d = coords.get(i) / points.size();
      String str = new DecimalFormat("#.0#").format(d);
      s += str + " ";
    }
    return s;
  }

  public void addPoint (Point p) {
    points.add(p);
  }

  public boolean containsPoint (Point p) {
    return points.contains(p);
  }

  public Set<Point> getPoints () {
    return points;
  }

  public int numPoints () {
    return points.size();
  }

  public Interval getInterval () {
    return interval;
  }

  public int getIndex () {
    return index;
  }

  public String printPoints () {
    StringJoiner sj = new StringJoiner("; ");
    for (Point p : points) sj.add(p.toString());
    return sj.toString();
  }

  @Override
  public String toString () {
    return interval.toString() + " #" + index + " (" + getAveragePoint() + ")";
  }

  @Override
  public int compareTo (Cluster other) {
    return interval.compareTo(other.getInterval());
  }

}
