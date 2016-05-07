package robo;

import java.awt.Point;
import java.util.ArrayList;
import robo.Utils.*;
//import static robo.Utils.*;

public class TangentBug {

    static class TangentGraph {

        private static boolean MiddleIsUseless(Polar p1, Polar p2, Polar p3) {
            final int sig1 = Integer.signum(p1.distance);
            final int sig2 = Integer.signum(p2.distance);
            final int sig3 = Integer.signum(p3.distance);
            return sig1 == sig2 && sig2 == sig3;
        }

        void Visit(int dist, double angle) {
            final Polar p = new Polar(dist, angle);
            if (Points.isEmpty()) {
                Points.add(p);
                return;
            }
            if (Points.size() >= 2 && MiddleIsUseless(Points.get(Points.size() - 2), Points.get(Points.size() - 1), p)) {
                Points.set(Points.size() - 1, p);
                return;
            }
            // add point
            Points.add(p);
        }

        void Finish() {
            if (Points.size() >= 2 && MiddleIsUseless(Points.get(Points.size() - 2), Points.get(Points.size() - 1), Points.get(0))) {
                Points.remove(Points.size() - 1);
            }
            if (Points.size() >= 2 && MiddleIsUseless(Points.get(Points.size() - 1), Points.get(0), Points.get(1))) {
                Points.remove(0);
            }
            if (Points.size() >= 2) {
                final Polar first = Points.get(0);
                final Polar last = Points.get(Points.size()-1);
                if (Integer.signum(first.distance)==Integer.signum(last.distance)) {
                    // Move last element to beginning
                    assert last.angle >= first.angle;
                    last.angle -= 2*Math.PI;
                    Points.remove(Points.size()-1);
                    Points.add(0, last);
                }
            }
            for (Polar p : Points) {
                System.out.println("Dist:" + p.distance + " angle:" + p.angle + " deg:" + p.angle * 360 / 2 / Math.PI);
            }
            
            // sanity check
            assert Utils.isEven(Points.size());
            for (int i=0; i<Points.size(); i+=2) {
                final Polar p1 = Points.get(i);
                final Polar p2 = Points.get(i+1);
                assert Integer.signum(p1.distance)==Integer.signum(p2.distance);
                assert p1.angle <= p2.angle;
                if (i != 0) {
                    final Polar prev = Points.get(i-1);
                    assert Integer.signum(prev.distance)!=Integer.signum(p1.distance);
                }
            }
            
            /*
            // Remove negative distances
            for (int i=0; i<Points.size(); i+=2) {
                Segments.add(new Segment(Points.get(i), Points.get(i+1)));
            }
            */
            
            //Points = null; // do not need them anymore
        }

        Polar GetBestRoute(Point robo, double robo_angle, Point goal) {
            // TODO goal is reacheable? FIXME
            Polar best = null;
            int best_dist = Integer.MAX_VALUE;
            for (Polar p : Points) {
                //System.out.println("polar: d:" + p.distance + " angle:" + p.angle);
                if (p.distance < 0) {
                    //System.out.println("skip");
                    continue;
                }
                final Point pd_local = Utils.PolarToDecart(p);
                final Point pd = new Point(pd_local.x + robo.x, pd_local.y + robo.y);
                final int dist = Utils.GetDist(robo, pd) + Utils.GetDist(pd, goal);
                //System.out.println("pd x=" + pd.x + " y=" + pd.y + " dist=" + dist);
                if (best == null || dist < best_dist) {
                    //System.out.println("replace best");
                    best = p;
                    best_dist = dist;
                }
            }
            return best;
        }

        private final ArrayList<Polar> Points = new ArrayList<>();
        //private final ArrayList<Segment> Segments = new ArrayList<>();
    }

}
