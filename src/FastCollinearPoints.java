/**
 * Created by josepcasado on 26/10/2017.
 */
import java.util.ArrayList;
import java.util.Comparator;

public class FastCollinearPoints {

    private class PointWithId {
        public Point point;
        public int id; // position in @points

        public PointWithId(Point p, int id) {
            this.point = p;
            this.id = id;
        }
    }

    private PointWithId[] points;
    private ArrayList<LineSegment> lineSegments = new ArrayList<>();

    // given the point id (position in @points) keeps a list with all the slopes of lineSegments that "contain" it.
    private ArrayList< ArrayList<Double> > checkedSlopes;

    public FastCollinearPoints(Point[] points) {

        if (points == null) throw new java.lang.IllegalArgumentException();

        this.points = new PointWithId[points.length];
        for (int i = 0; i < this.points.length; i++) this.points[i] = new PointWithId(points[i], i);

        this.checkedSlopes = new ArrayList< ArrayList<Double> >(points.length);
        for (int i = 0; i < points.length; i++) this.checkedSlopes.add(null);

        for (int i = 0; i < points.length; i++) {

            PointWithId p = getPoint(i);

            if (i+1 == points.length) break;

            PointWithId[] sortedPoints = sortPointsToP(p.point.slopeOrder(), i+1, points.length-1);
            sortedPoints = cleanSortedList(p, sortedPoints);
            if (sortedPoints.length == 0) break;

            int pointsCount = 1;
            Double lastSlope = p.point.slopeTo(sortedPoints[0].point);

            PointWithId smallestPoint, biggestPoint;
            smallestPoint = biggestPoint = p;

            ArrayList<Integer> sameSlopeIds = new ArrayList<>();
            sameSlopeIds.add(p.id); sameSlopeIds.add(sortedPoints[0].id);

            for (PointWithId q : sortedPoints) {
                double slope = p.point.slopeTo(q.point);
                if (slope == lastSlope) {
                    pointsCount++;
                    if (q.point.compareTo(smallestPoint.point) < 0) smallestPoint = q;
                    else if (q.point.compareTo(biggestPoint.point) > 0) biggestPoint = q;
                } else {
                    if (pointsCount >= 4) {
                        this.lineSegments.add(new LineSegment(smallestPoint.point, biggestPoint.point));
                        addSlopeToCheckedSlopes(lastSlope, sameSlopeIds);
                    }

                    pointsCount = 2;

                    smallestPoint = biggestPoint = p;
                    if (q.point.compareTo(smallestPoint.point) < 0) smallestPoint = q;
                    else if (q.point.compareTo(biggestPoint.point) > 0) biggestPoint = q;

                    sameSlopeIds = new ArrayList<>();
                    sameSlopeIds.add(p.id); sameSlopeIds.add(q.id);
                }
                lastSlope = slope;
            }
            if (pointsCount >= 4) {
                this.lineSegments.add(new LineSegment(smallestPoint.point, biggestPoint.point));
                addSlopeToCheckedSlopes(lastSlope, sameSlopeIds);
            }

        }

    }

    public int numberOfSegments() {
        return this.lineSegments.size();
    }

    public LineSegment[] segments() {
        return this.lineSegments.toArray(new LineSegment[lineSegments.size()]);
    }

    private PointWithId getPoint(int i) {
        PointWithId p = this.points[i];
        if (p.point == null) throw new java.lang.IllegalArgumentException();
        return p;
    }

    private Double getSlope(Point p1, Point p2) {
        Double slope = p1.slopeTo(p2);
        if (slope == Double.NEGATIVE_INFINITY) throw new java.lang.IllegalArgumentException();
        return slope;
    }

    private PointWithId[] sortPointsToP(Comparator<Point> comparator, int from, int to) {

        // Base case
        if (to == from) {
            return new PointWithId[] {getPoint(to)};
        }
        else if (to - from == 1) {

            PointWithId a = getPoint(from);
            PointWithId b = getPoint(to);
            if (a.point.compareTo(b.point) == 0) throw new java.lang.IllegalArgumentException();

            if (comparator.compare(a.point, b.point) <= 0) return new PointWithId[]{a, b};
            else return new PointWithId[]{b, a};

        } else {

            int divide = from+(to-from)/2;
            PointWithId[] sortLeft = sortPointsToP(comparator, from, divide);
            PointWithId[] sortRight = sortPointsToP(comparator, divide+1, to);
            return join(comparator, sortLeft, sortRight);

        }

    }

    private PointWithId[] join(Comparator<Point> comparator, PointWithId[] left, PointWithId[] right) {

        PointWithId[] result = new PointWithId[left.length + right.length];
        int leftIt, rightIt; leftIt = rightIt = 0;
        PointWithId leftPoint = left[leftIt];
        PointWithId rightPoint = right[rightIt];
        while (leftIt < left.length && rightIt < right.length) {
            if (comparator.compare(leftPoint.point, rightPoint.point) <= 0) {
                result[leftIt + rightIt] = leftPoint;
                leftIt++;
                if (leftIt != left.length) leftPoint = left[leftIt];
            } else {
                result[leftIt + rightIt] = rightPoint;
                rightIt++;
                if (rightIt != right.length) rightPoint = right[rightIt];
            }
        }

        int it, offset; PointWithId[] leftPoints;
        if (leftIt == left.length) { it = rightIt; offset = left.length; leftPoints = right; }
        else { it = leftIt; offset = right.length; leftPoints = left; }
        for (; it < leftPoints.length; it++) result[offset + it] = leftPoints[it];

        return result;

    }

    private void printSlopeList(Point p, PointWithId[] points) {
        for (PointWithId q : points) {
            System.out.print(" " + p.slopeTo(q.point));
        }
        System.out.println();
    }

    private void addSlopeToCheckedSlopes(double slope, ArrayList<Integer> whichPoints) {
        for (int i : whichPoints) {
            if (this.checkedSlopes.get(i) == null) this.checkedSlopes.set(i, new ArrayList<Double>());
            this.checkedSlopes.get(i).add(slope);
        }
    }

    private boolean pointHasSlope(int id, double slopeToCheck) {
        if (this.checkedSlopes.get(id) == null) return false;
        return ( (slopeToCheck == -0.0 || slopeToCheck == 0.0) ? this.checkedSlopes.get(id).contains(slopeToCheck) || this.checkedSlopes.get(id).contains(-slopeToCheck) :
                                                                this.checkedSlopes.get(id).contains(slopeToCheck));
    }

    private PointWithId[] cleanSortedList(PointWithId p, PointWithId[] sortedPoints) {
        ArrayList<PointWithId> result = new ArrayList<>();
        for (PointWithId q : sortedPoints) if (!pointHasSlope(p.id, q.point.slopeTo(p.point))) result.add(q);
        return result.toArray(new PointWithId[result.size()]);
    }

}
