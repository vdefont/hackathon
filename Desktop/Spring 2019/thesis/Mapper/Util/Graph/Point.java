package Util.Graph;

import java.util.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class Point {

  private List<Double> coords;
  private String label;

  public Point (List<Double> coords) {
    this.coords = coords;
    setDefaultLabel();
  }

  public Point (double[] coords) {
    this.coords = new ArrayList<Double>();
    for (int i = 0; i < coords.length; i += 1) {
      this.coords.add(coords[i]);
    }
    setDefaultLabel();
  }

  private void setDefaultLabel () {
    StringJoiner sj = new StringJoiner(", ");
    for (double d : coords) {
      sj.add("" + d);
    }
    this.label = "(" + sj.toString() + ")";
  }

  public void setLabel (String label) {
    this.label = label;
  }

  public int getX () {
    if (coords.size() > 0) return coords.get(0).intValue();
    return 0;
  }

  public int getY () {
    if (coords.size() > 1) return coords.get(1).intValue();
    return 0;
  }

  public List<Double> getCoords () {
    return coords;
  }

  public double euclidianDistTo (Point other) {
    double sumOfSquares = 0.0;
    List<Double> coords = getCoords();
    List<Double> otherCoords = other.getCoords();

    int numCoords = coords.size();
    for (int i = 0; i < numCoords; i += 1) {
      sumOfSquares += Math.pow(coords.get(i) - otherCoords.get(i), 2.0);
    }
    return Math.pow(sumOfSquares, 0.5);
  }

  @Override
  public String toString () {
    return label;
  }

}
