import java.util.*;
import java.io.*;

import Util.Graph.*;
import Util.*;

class EducationMapper extends Mapper {

  // Good for the laplacian
  private static final double
    INTERVAL_SPACING = 0.005,
    INTERVAL_WIDTH = 0.007,
    LEFT_BOUND = -0.05,
    RIGHT_BOUND = 0.01;

  // Good for eccentricity
  // private static final double
  //   INTERVAL_SPACING = 1,
  //   INTERVAL_WIDTH = 1.2,
  //   LEFT_BOUND = 0,
  //   RIGHT_BOUND = 15;

  private static final double CLUSTER_DIST = 3;

  private static final String FILENAME = "EducationMapperLaplacian";

  @Override
  public List<Point> getPoints () {
    List<Point> points = new ArrayList<Point>();

    try (BufferedReader br = new BufferedReader(new FileReader("data/university/clean.csv"))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] fields = line.split(",");
        double[] doubles = new double[9];
        for (int i = 12; i <= 20; i += 1) {
          doubles[i-12] = Double.valueOf(fields[i]);
        }
        Point p = new Point(doubles);
        String collegeName = fields[1];
        p.setLabel(collegeName);
        points.add(p);
      }
    } catch (IOException e) {
      System.out.println("Error reading file: " + e);
    }

    return points;
  }

  // Called by filterAll
  private Map<Point, Double> filterSelection (List<Point> points) {
    return Filter.laplacianFilter(points);
  }

  @Override
  public Map<Point, Double> filterAll (List<Point> points) {
    List<Double> doubles;
    Map<Point, Double> filter;
    int numPoints = points.size();

    // If we already stored filter, read it in
    if (Filter.fileExistsInCache(FILENAME)) {
      doubles = Filter.readCachedFileAsDoubles(FILENAME);
      filter = new HashMap<Point, Double>();
      for (int i = 0; i < numPoints; i += 1) {
        Point p = points.get(i);
        Double d = doubles.get(i);
        filter.put(p, d);
      }
    }

    // If filter doesn't exist, compute and store
    else {
      doubles = new ArrayList<>();
      filter = filterSelection(points);
      for (Point p : filter.keySet()) {
        Double d = filter.get(p);
        doubles.add(d);
      }
      Filter.writeCachedFileAsDoubles(FILENAME, doubles);
    }

    return filter;
  }

  @Override
  public List<Interval> getCover (Map<Point, Double> filterVals) {
    List<Interval> intervals = new ArrayList<Interval>();
    for (double start = LEFT_BOUND; start <= RIGHT_BOUND; start += INTERVAL_SPACING) {
      intervals.add(new Interval(start, start + INTERVAL_WIDTH));
    }
    return intervals;
  }

  @Override
  public boolean inSameCluster (Point a, Point b) {
    return distance(a, b) < CLUSTER_DIST;
  }

  public EducationMapper () {
    super();
  }

  public static void main (String[] args) {
    new EducationMapper();
  }

}
