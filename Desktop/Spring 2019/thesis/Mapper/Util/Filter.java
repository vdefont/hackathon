package Util;

import java.util.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.io.*;

import Jama.*;
import Util.Graph.*;

public class Filter {


  // Default p = 2.0
  public static Map<Point, Double> eccentricityFilter (List<Point> points) {
    return eccentricityFilter(points, 2.0);
  }

  // Eccentricity of x: ( 1/N * sum(d(x,y)^p) ) ^ (1/p)
  public static Map<Point, Double> eccentricityFilter (List<Point> points, double p) {

    int numPoints = points.size();

    Map<Point, Double> filter = new HashMap<>();
    for (Point x : points) {

      double sum = 0.0;
      for (Point y : points) {
        double dist = x.euclidianDistTo(y);
        sum += Math.pow(dist, p);
      }
      double xEccentricity = Math.pow(sum / numPoints, 1.0 / p);
      filter.put(x, xEccentricity);
    }

    return filter;
  }

  public static Map<Point, Double> laplacianFilter (List<Point> points) {

    // Calculate laplacian

    // Calculate edge weights and degrees
    int numPoints = points.size();
    double[][] edgeWeights = new double[numPoints][numPoints];
    double[] degrees = new double[numPoints];
    for (int i = 0; i < numPoints; i += 1) degrees[i] = 0.0;
    for (int i = 0; i < numPoints; i += 1) {
      for (int j = i+1; j < numPoints; j += 1) {
        Point pI = points.get(i);
        Point pJ = points.get(j);
        double dist = pI.euclidianDistTo(pJ);
        double edgeWeight = Util.gaussianPdf(dist);
        edgeWeights[i][j] = edgeWeight;
        edgeWeights[j][i] = edgeWeight;
        degrees[i] += edgeWeight;
        degrees[j] += edgeWeight;
      }
    }

    // Calculate laplacian
    double[][] laplacianVals = new double[numPoints][numPoints];
    for (int i = 0; i < numPoints; i += 1) {
      for (int j = 0; j < numPoints; j += 1) {
        if (i == j) {
          laplacianVals[i][j] = 1.0;
        } else {
          laplacianVals[i][j] = -1 * edgeWeights[i][j] / Math.sqrt(degrees[i] * degrees[j]);
        }
      }
    }

    Matrix laplacian = new Matrix(laplacianVals);

    // Compute and store first eigenvector
    EigenvalueDecomposition e = laplacian.eig();
    Matrix evectors = e.getV();
    Matrix evals = e.getD();
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("evectors"));
      for (int row = 0; row < numPoints; row += 1) {
        String s = "";
        for (int col = 1; col <= 4; col += 1) {
          double d = evectors.get(row, col) / Math.pow(degrees[row], 0.5);
          s += d + ",";
        }
        writer.write(s);
        writer.newLine();
      }
      writer.close();
    } catch (IOException ee) {
      System.out.println("Error writing file: " + ee);
    }

    // Store first eigenvector (first column)
    HashMap<Point, Double> map = new HashMap<>();
    for (int i = 0; i < numPoints; i += 1) {
      Point p = points.get(i);
      double val = evectors.get(i, 0); // x-coordinate
      map.put(p, val);
    }
    return map;
  }

  private static String cacheFilePath (String fileName) {
    return "cache" + File.separator + fileName;
  }

  public static boolean fileExistsInCache (String fileName) {
    String filePath = cacheFilePath(fileName);
    File f = new File(filePath);
    return f.exists();
  }

  public static List<Double> readCachedFileAsDoubles (String fileName) {
    List<Double> doubles = new ArrayList<>();
    String filePath = cacheFilePath(fileName);
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        Double d = new Double(line);
        doubles.add(d);
      }
    } catch (IOException e) {
      System.out.println("Error reading file: " + e);
    }
    return doubles;
  }

  public static void writeCachedFileAsDoubles (String fileName, List<Double> doubles) {
    String filePath = cacheFilePath(fileName);
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
      for (Double d : doubles) {
        writer.write("" + d);
        writer.newLine();
      }
      writer.close();
    } catch (IOException e) {
      System.out.println("Error writing file: " + e);
    }
  }

}
