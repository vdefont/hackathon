import java.util.*;

import Util.Graph.*;
import Util.*;

class Mapper {

  // USER-DEFINED FUNCTIONS

  public List<Point> getPoints () {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(new double[] {0,0}));
    points.add(new Point(new double[] {1,2}));
    points.add(new Point(new double[] {1,4}));
    points.add(new Point(new double[] {3,1}));

    // Set up filter function

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
    double[][] laplacian = new double[numPoints][numPoints];
    for (int i = 0; i < numPoints; i += 1) {
      for (int j = 0; j < numPoints; j += 1) {
        if (i == j) {
          laplacian[i][j] = 1.0;
        } else {
          laplacian[i][j] = -1 * edgeWeights[i][j] / Math.sqrt(degrees[i] * degrees[j]);
        }
      }
    }

    // TODO put laplacian into matrix and get evector

    return points;
  }

  public double filter (Point p) {
    return p.getCoords().get(0);
  }

  // Filters all points
  public Map<Point, Double> filterAll (List<Point> points) {
    HashMap<Point, Double> map = new HashMap<>();
    for (Point p : points) {
      double val = filter(p); // x-coordinate
      map.put(p, val);
    }
    return map;
  }

  public List<Interval> getCover (Map<Point, Double> filterVals) {
    List<Interval> intervals = new ArrayList<Interval>();
    intervals.add(new Interval(0.5, 3.5));
    intervals.add(new Interval(2.5, 5.5));
    intervals.add(new Interval(4.5, 7.5));
    return intervals;
  }

  // Distance metric - simple euclidian distance
  public double distance (Point a, Point b) {
    return a.euclidianDistTo(b);
  }

  public boolean inSameCluster (Point a, Point b) {
    return distance(a, b) < 1.5;
  }

  public List<Cluster> cluster (List<Point> points, Interval interval) {

    // Initialize unionFind
    UnionFind<Point> uf = new UnionFind<Point>(new HashSet<Point>());
    for (Point p : points) {
      uf.addElement(p);
    }

    // Unify points
    int numPoints = points.size();
    for (int i = 0; i < numPoints; i += 1) {
      for (int j = i+1; j < numPoints; j += 1) {
        Point pI = points.get(i);
        Point pJ = points.get(j);
        if (inSameCluster(pI, pJ)) {
          uf.union(pI, pJ);
        }
      }
    }

    // Associate all points with their cluster
    Map<Point, Cluster> parentCluster = new HashMap<>();
    int index = 0;
    for (Point p : points) {
      Point parent = uf.find(p);
      if (!parentCluster.containsKey(parent)) {
        index += 1;
        parentCluster.put(parent, new Cluster(interval, index));
      }
      parentCluster.get(parent).addPoint(p);
    }

    // Put into list and return
    List<Cluster> clusters = new ArrayList<>();
    for (Cluster c : parentCluster.values()) {
      clusters.add(c);
    }
    return clusters;
  }



  // STANDARD IMPLEMENTATIONS

  // Builds and populates intervals that cover image of filtered points
  private Map<Interval, List<Point>> buildCover (
    Map<Point, Double> filterVals,
    List<Interval> intervals
  ) {

    // Initialize map
    Map<Interval, List<Point>> cover = new HashMap<>();
    for (Interval i : intervals) {
      cover.put(i, new ArrayList<Point>());
    }

    // Add points to map
    for (Point p : filterVals.keySet()) {
      for (Interval i : intervals) {
        if (i.contains(filterVals.get(p))) {
          cover.get(i).add(p);
        }
      }
    }

    return cover;
  }

  // Builds clusters in each interval
  private List<Cluster> clusterAll (Map<Interval, List<Point>> cover) {
    // Build clusters
    List<Cluster> clusters = new ArrayList<>();
    for (Interval i : cover.keySet()) {
      clusters.addAll(cluster(cover.get(i), i));
    }
    // Sort clusters
    Cluster[] clustersArr = new Cluster[clusters.size()];
    for (int i = 0; i < clusters.size(); i += 1) {
      clustersArr[i] = clusters.get(i);
    }
    Arrays.sort(clustersArr);
    return Arrays.asList(clustersArr);
  }

  // Get all cluster conections
  private List<Edge<Cluster>> getClusterConnections (List<Cluster> clusters, List<Point> points) {

    int numClusters = clusters.size();

    // Initialize cluster map
    Map<Cluster, Set<Cluster>> connections = new HashMap<>();
    for (Cluster c : clusters) connections.put(c, new HashSet<>());
    // Populate cluster map
    for (int i = 0; i < numClusters; i += 1) {
      for (int j = i+1; j < numClusters; j += 1) {
        // Determine which is smaller cluster
        Cluster small = clusters.get(i);
        Cluster large = clusters.get(j);
        if (large.numPoints() < small.numPoints()) {
          Cluster temp = small;
          small = large;
          large = temp;
        }
        for (Point p : small.getPoints()) {
          if (large.containsPoint(p)) {
            connections.get(small).add(large);
          }
        }
      }
    }

    // Convert map to edge list
    List<Edge<Cluster>> ret = new ArrayList<>();
    for (Cluster src : connections.keySet()) {
      for (Cluster dst : connections.get(src)) {
        Edge<Cluster> e = new Edge<Cluster>(src, dst);
        ret.add(e);
      }
    }
    return ret;
  }

  // GRAPHICS HELPERS
  Map<Cluster, Point> getClusterLocations (List<Cluster> clusters) {
    // Get cluster locations
    Interval lastInterval = null;
    double xi = 0.0;
    Map<Cluster, Point> clusterLocations = new HashMap<Cluster, Point>();
    for (Cluster c : clusters) {
      Interval curInterval = c.getInterval();
      if (curInterval != lastInterval) {
        lastInterval = curInterval;
        xi += 1;
      }
      double yi = 0.0 + c.getIndex();
      Point point = new Point(new double[]{xi, yi});
      clusterLocations.put(c, point);
    }
    return clusterLocations;
  }
  List<Point> makeVertices (List<Cluster> clusters, Map<Cluster, Point> clusterLocations) {
    List<Point> vertices = new ArrayList<>();
    for (Cluster c : clusters) {
      vertices.add(clusterLocations.get(c));
    }
    return vertices;
  }
  List<Edge<Point>> makeEdges (
    List<Cluster> clusters,
    Map<Cluster, Point> clusterLocations,
    List<Edge<Cluster>> connections
  ) {
    List<Edge<Point>> edges = new ArrayList<>();
    for (Edge<Cluster> e : connections) {
      Cluster srcCluster = e.getSrc();
      Cluster dstCluster = e.getDst();
      Point srcPoint = clusterLocations.get(srcCluster);
      Point dstPoint = clusterLocations.get(dstCluster);
      Edge<Point> newEdge = new Edge<Point>(srcPoint, dstPoint);
      edges.add(newEdge);
    }
    return edges;
  }

  public Mapper () {

    // Get points
    List<Point> points = getPoints();
    // Filter points
    Map<Point, Double> filterVals = filterAll(points);

    // Get intervals
    List<Interval> intervals = getCover(filterVals);
    // Build cover
    Map<Interval, List<Point>> cover = buildCover(filterVals, intervals);

    // Build all clusters
    List<Cluster> clusters = clusterAll(cover);
    // Draw edges between clusters
    List<Edge<Cluster>> connections = getClusterConnections(clusters, points);


    // Make graphics

    // Get cluster locations
    Map<Cluster, Point> clusterLocations = getClusterLocations(clusters);
    // Make vertices and edges
    List<Point> vertices = makeVertices(clusters, clusterLocations);
    List<Edge<Point>> edges = makeEdges(clusters, clusterLocations, connections);

    // Call graphics class
    new GraphMaker(vertices, edges, clusters);

  }

  public static void main (String[] args) {
    new Mapper();
  }

}
