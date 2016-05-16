package robo;

import java.awt.Point;

public class Utils {

    static class Polar {
        int distance;
        double angle;
        Polar(int d, double a) { distance=d; angle=a; }
    }

    // polar coordinates to obstacle taking into account where the obstacle is - on the left
    static class PolarTurn {
        Polar polar;
        int turnSign; // {-1,+1}
        PolarTurn(Polar p, int sign) { polar=p; turnSign=sign; }
    }

    // convert mathematically calculated values to reality:
    // - correct angle, so robot moves slightly aside from obstacle's point, not directly to it (robot have non-zero size)
    // - slightly increase distance, so robot moves slightly farther than obstacle's point
    static Polar ToRobotMotion(PolarTurn p, int roboSize)
    {
        double angleCorrection = Math.asin((double)(roboSize*2)/(double)p.polar.distance);
        if (p.turnSign < 0)
            angleCorrection = -angleCorrection;
        final int distCorrection = 2 * roboSize;
        return new Polar(p.polar.distance + distCorrection, p.polar.angle + angleCorrection);
    }

    // Converts angle to [-pi, pi] interval. углы в радианах
    // влево не более чем на 180 и вправо не более чем на 180
    static double NormalizeAngle(double a) {
        while (a > Math.PI) {
            a -= 2 * Math.PI;
        }
        while (a < -Math.PI) {
            a += 2 * Math.PI;
        }
        return a;
    }

    static Point PolarToDecart(Polar p) {
        double dd = (double) p.distance;
        return new Point((int) (dd * Math.cos(p.angle)), (int) (dd * Math.sin(p.angle)));
    }

    static int GetDist(Point p1, Point p2) {
        return (int) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    static boolean isEven(int i) {
        return (i & 1) == 0;
    }

    //to find angle of direction from p1 to p2
    static double ComputeAngle(final Point p1, final Point p2) {
        return Math.atan2((double)(p2.y - p1.y), (double)(p2.x - p1.x));
    }
}
