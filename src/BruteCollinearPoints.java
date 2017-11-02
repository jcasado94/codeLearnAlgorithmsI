/**
 * Created by josepcasado on 26/10/2017.
 */
import java.util.ArrayList;


public class BruteCollinearPoints {

    private ArrayList<LineSegment> lineSegments = new ArrayList<>();

    public BruteCollinearPoints(Point[] points) {

        if (points == null) throw new java.lang.IllegalArgumentException();

        for (int i = 0; i < points.length; i++) {

            Point p = getPoint(i, points);

            for (int j = i+1; j < points.length; j++) {

                Point q = getPoint(j, points);
                Double slopePQ = getSlope(p, q);

                for (int k = j+1; k < points.length; k++) {

                    Point r = getPoint(k, points);
                    Double slopeQR = getSlope(q, r);

                    for (int l = k+1; l < points.length; l++) {

                        Point s = getPoint(l, points);
                        Double slopeRS = getSlope(r, s);

                        if (slopePQ.equals(slopeQR) && slopeQR.equals(slopeRS)) {
                            lineSegments.add(new LineSegment(p, s));
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
        return slope;
    }

}
