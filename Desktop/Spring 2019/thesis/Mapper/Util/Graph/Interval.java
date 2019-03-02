package Util.Graph;

import java.util.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class Interval implements Comparable<Interval> {

  private double start, end;

  public Interval (double start, double end) {
    this.start = start;
    this.end = end;
  }

  public double getStart () {
    return start;
  }

  public double getEnd () {
    return end;
  }

  public boolean contains (double d) {
    return d >= start && d <= end;
  }

  @Override
  public int compareTo (Interval other) {
    return Double.compare(start, other.getStart());
  }

  @Override
  public String toString () {
    DecimalFormat df = new DecimalFormat("#.##");
    df.setRoundingMode(RoundingMode.FLOOR);
    return "[" + df.format(start) + ", " + df.format(end) + "]";
  }
}
