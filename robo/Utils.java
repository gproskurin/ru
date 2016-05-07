package robo;

import java.awt.Point;

public class Utils {

    static class Polar {
        int distance;
        double angle;
        Polar(int d, double a) { distance=d; angle=a; }
    }
    
    static class Segment {
        Polar begin;
        Polar end;
        Segment(Polar b, Polar e) { begin=b; end=e; }
    }
    
    // Converts angle to [-pi, pi] interval
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
    
}
