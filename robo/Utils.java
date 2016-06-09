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
        boolean cutDist;
        PolarTurn(Polar p, int sign, boolean cut) { polar=p; turnSign=sign; cutDist=cut; }
    }

    static class FollowWallDirection {
        boolean WallOnTheRight;
        double angle;
        FollowWallDirection(boolean wr, double a) {
            WallOnTheRight = wr;
            angle = a;
        }
    }

    // convert mathematically calculated values to reality:
    // - correct angle, so robot moves slightly aside from obstacle's point, not directly to it (robot have non-zero size)
    // - slightly increase distance, so robot moves slightly farther than obstacle's point
    static Polar ToRobotMotion(PolarTurn p, int roboSize)
    {
        double angleCorrection = Math.asin((double)(roboSize*2)/(double)p.polar.distance);
        if (p.turnSign < 0)
            angleCorrection = -angleCorrection;
        final int distCorrection = p.cutDist ? 0 : roboSize;
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

    static int GetDist(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    static int GetDist(int x1, int y1, Point p2) {
        return GetDist(x1, y1, p2.x, p2.y);
    }

    static int GetDist(Point p1, Point p2) {
        return GetDist(p1.x, p1.y, p2.x, p2.y);
    }

    static Point DecartFromPoint(int x, int y, Polar pp) {
        final Point pp_dec = PolarToDecart(pp);
        return new Point(x + pp_dec.x, y + pp_dec.y);
    }

    static boolean isEven(int i) {
        return (i & 1) == 0;
    }

    //to find angle of direction from p1 to p2
    static double ComputeAngle(int x1, int y1, int x2, int y2) {
        return Math.atan2((double)(y2 - y1), (double)(x2 - x1));
    }

    static double deg2rad(int deg)
    {
        return Math.PI / 180 * deg;
    }

    static int rad2deg(double rad)
    {
        return (int) (rad / Math.PI * 180);
    }
}
