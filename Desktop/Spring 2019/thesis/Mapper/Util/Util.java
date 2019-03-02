package Util;

import java.util.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class Util {

  // Applies gaussian function (mean 0, standard dev. 1)
  public static double gaussianPdf (double x) {
    return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
  }

}
