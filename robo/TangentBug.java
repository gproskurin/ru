package robo;

import java.awt.Point;
import java.util.ArrayList;
import robo.Utils.*;

public class TangentBug {

    static class TangentGraph {

        private final ArrayList<Polar> Polars = new ArrayList<>();

        private static boolean MiddleIsUseless(Polar p1, Polar p2, Polar p3) {
            final int sig1 = Integer.signum(p1.distance);
            final int sig2 = Integer.signum(p2.distance);
            final int sig3 = Integer.signum(p3.distance);
            return sig1 == sig2 && sig2 == sig3;
        }

        void Visit(int dist, double absoluteAngle) {
            final Polar p = new Polar(dist, absoluteAngle);
            if (Polars.isEmpty()) {
                Polars.add(p);
                return;
            }
            if (Polars.size() >= 2 && MiddleIsUseless(Polars.get(Polars.size() - 2), Polars.get(Polars.size() - 1), p)) {
                // replace last point
                Polars.set(Polars.size() - 1, p);
                return;
            }
            // add point
            Polars.add(p);
        }

        void Finish() {
            if (Polars.size() >= 2 && MiddleIsUseless(Polars.get(Polars.size() - 2), Polars.get(Polars.size() - 1), Polars.get(0))) {
                Polars.remove(Polars.size() - 1);
            }
            if (Polars.size() >= 2 && MiddleIsUseless(Polars.get(Polars.size() - 1), Polars.get(0), Polars.get(1))) {
                Polars.remove(0);
            }
            if (Polars.size() >= 2) {
                final Polar first = Polars.get(0);
                final Polar last = Polars.get(Polars.size()-1);
                if (Integer.signum(first.distance)==Integer.signum(last.distance)) {
                    // Move last element to beginning
                    assert last.angle >= first.angle;
                    last.angle -= 2*Math.PI;
                    Polars.remove(Polars.size()-1);
                    Polars.add(0, last);
                }
            }
            for (Polar p : Polars) {
                System.out.println("Dist:" + p.distance + " angle:" + p.angle + " deg:" + p.angle * 360 / 2 / Math.PI);
            }

            // sanity check
            assert Utils.isEven(Polars.size());
            for (int i=0; i<Polars.size(); i+=2) {
                final Polar p1 = Polars.get(i);
                final Polar p2 = Polars.get(i+1);
                assert Integer.signum(p1.distance)==Integer.signum(p2.distance);
                assert p1.angle <= p2.angle;
                if (i != 0) {
                    final Polar prev = Polars.get(i-1);
                    assert Integer.signum(prev.distance)!=Integer.signum(p1.distance);
                    assert prev.angle <= p1.angle;
                }
            }

            // Remove negative distances
            for (int i=Polars.size()-1; i>=0; --i) {
                if (Polars.get(i).distance < 0)
                    Polars.remove(i);
            }

            System.out.println("After removing:");
            for (Polar p : Polars) {
                System.out.println("Dist:" + p.distance + " angle:" + p.angle + " deg:" + p.angle * 360 / 2 / Math.PI);
            }
        }

        PolarTurn GetBestRoute(Point robo, double robo_angle, Point goal) {
            // TODO goal is reacheable? FIXME
            assert !Polars.isEmpty();
            int bestIdx = -1;
            int bestDist = Integer.MAX_VALUE;
            for (int i=0; i<Polars.size(); ++i) {
                final Polar p = Polars.get(i);
                assert p.distance >= 0;

                final Point delta = Utils.PolarToDecart(p); // shift from current robot position
                final Point obstaclePoint = new Point(robo.x + delta.x, robo.y + delta.y);
                final int dist = Utils.GetDist(robo, obstaclePoint) + Utils.GetDist(obstaclePoint, goal);
                if (dist < bestDist) {
                    bestIdx = i;
                    bestDist = dist;
                }
            }

            // Used later to calculate angle correction. See ToRobotMotion() function for details.
            // Even index means begin of obstacle sector, angle correction should be negative
            final int turnSign = Utils.isEven(bestIdx) ? -1 : 1;

            return new PolarTurn(Polars.get(bestIdx), turnSign);
        }
    }

}
