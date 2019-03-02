package Util.Graph;

import java.util.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class Edge<T> {
  private T src, dst;

  public Edge (T src, T dst) {
    this.src = src;
    this.dst = dst;
  }

  public T getSrc() {
    return src;
  }

  public T getDst() {
    return dst;
  }

  @Override
  public String toString () {
    return "(" + src.toString() + ", " + dst.toString() + ")";
  }
}
