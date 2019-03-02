import java.util.*;

import Util.Graph.*;
import Util.*;

class TestMapper extends Mapper {

  @Override
  public List<Point> getPoints () {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(new double[] {0,0}));
    points.add(new Point(new double[] {1,2}));
    points.add(new Point(new double[] {1,4}));
    points.add(new Point(new double[] {3,1}));
    return points;
  }

  // Filters all points
  @Override
  public Map<Point, Double> filterAll (List<Point> points) {
    return Filter.laplacianFilter(points);
  }

  @Override
  public List<Interval> getCover (Map<Point, Double> filterVals) {
    List<Interval> intervals = new ArrayList<Interval>();
    intervals.add(new Interval(0, 0.7));
    intervals.add(new Interval(0.5, 1.2));
    intervals.add(new Interval(1.0, 1.7));
    return intervals;
  }

  public TestMapper () {
    super();
  }

  public static void main (String[] args) {
    new TestMapper();
  }

}
