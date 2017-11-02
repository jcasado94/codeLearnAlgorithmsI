/**
 * Created by josepcasado on 26/10/2017.
 */
import java.util.ArrayList;


public class BruteCollinearPoints {

    private ArrayList<LineSegment> lineSegments = new ArrayList<>();
    private ArrayList<Double>[] checkedSlopes;

    public BruteCollinearPoints(Point[] points) {

        if (points == null) throw new java.lang.IllegalArgumentException();
        checkedSlopes = new ArrayList[points.length];

        for (int i = 0; i < points.length; i++) {

            Point p = getPoint(i, points);

            for (int j = i+1; j < points.length; j++) {

                Point q = getPoint(j, points);
                Double slopePQ = getSlope(p, q);
                if (hasSlope(i, slopePQ) || hasSlope(j, slopePQ)) continue;

                for (int k = j+1; k < points.length; k++) {

                    Point r = getPoint(k, points);
                    Double slopeQR = getSlope(q, r);
                    if (!slopePQ.equals(slopeQR) || hasSlope(k, slopeQR)) continue;

                    for (int l = k+1; l < points.length; l++) {

                        Point s = getPoint(l, points);
                        Double slopeRS = getSlope(r, s);

                        if (slopeQR.equals(slopeRS) && !hasSlope(l, slopeRS)) {
                            Point[] smallestBiggestPoints = getSmallestAndBiggestPoints(i, j, k, l, points);
                            lineSegments.add(new LineSegment(smallestBiggestPoints[0], smallestBiggestPoints[1]));
                            addSlope(i, slopePQ); addSlope(j, slopePQ); addSlope(k, slopePQ); addSlope(l, slopePQ);
                        }
                    }
                }
            }
        }

    }

    public int numberOfSegments() {
        return lineSegments.size();
    }

    public LineSegment[] segments() {
        return lineSegments.toArray(new LineSegment[lineSegments.size()]);
    }

    private Point getPoint(int i, Point[] points) {
        Point p = points[i];
        if (p == null) throw new java.lang.IllegalArgumentException();
        return p;
    }

    private Double getSlope(Point p1, Point p2) {
        Double slope = p1.slopeTo(p2);
        if (slope == Double.NEGATIVE_INFINITY) throw new java.lang.IllegalArgumentException();
        return (slope == -0.0 ? 0.0 : slope);
    }

    private Point[] getSmallestAndBiggestPoints(int i, int j, int k, int l, Point[] points) {

        Point smallest, biggest;
        smallest = biggest = points[i];

        if (points[j].compareTo(smallest) < 0) smallest = points[j];
        else if (points[j].compareTo(biggest) > 0) biggest = points[j];
        if (points[k].compareTo(smallest) < 0) smallest = points[k];
        else if (points[k].compareTo(biggest) > 0) biggest = points[k];
        if (points[l].compareTo(smallest) < 0) smallest = points[l];
        else if (points[l].compareTo(biggest) > 0) biggest = points[l];

        return new Point[]{smallest, biggest};
    }

    private boolean hasSlope(int i, double slope) {

        if (checkedSlopes[i] == null) return false;
        return checkedSlopes[i].contains(slope);

    }

    private void addSlope(int i, double slope) {
        if (checkedSlopes[i] == null) checkedSlopes[i] = new ArrayList<>();
        if (slope == -0.0) slope = 0.0;
        checkedSlopes[i].add(slope);
    }

}
