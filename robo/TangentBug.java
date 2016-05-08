package robo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Optional;
import robo.Utils.*;

public class TangentBug {

    static void FollowWall() {
        // TODO
    }

    static class TangentGraph {

        private final ArrayList<Polar> Nodes = new ArrayList<>();
        private Optional<Boolean> startedInsideSector = Optional.empty();
        private boolean insideSector = false;

        void Visit(int dist, double absoluteAngle) {
            System.out.println("Visit: dist:"+dist+" angle:"+absoluteAngle);

            if (!startedInsideSector.isPresent()) {
                assert Nodes.isEmpty();
                System.out.println("first visit: dist:"+dist);
                startedInsideSector = Optional.of(dist >= 0);
            }

            if (!insideSector) {
                assert (Utils.isEven(Nodes.size()));
            }

            if (dist < 0) {
                // end of current sector (if any)
                System.out.println("end-of-sector");

                if (!Utils.isEven(Nodes.size())) {
                    // sector started, but not ended (very unlikely)
                    // Duplicate last element
                    assert insideSector;
                    Nodes.add(Nodes.get(Nodes.size()-1));
                }
                assert Utils.isEven(Nodes.size());
                insideSector = false;
                return;
            }

            final Polar p = new Polar(dist, absoluteAngle);

            if (!insideSector || !Utils.isEven(Nodes.size())) {
                // begin of new sector OR second point of last sector
                // just append it
                //assert Utils.isEven(Nodes.size());
                System.out.println("append");
                Nodes.add(p);
                insideSector = true;
                return;
            }

            // third point of current sector, replace last point
            assert !Nodes.isEmpty();
            assert Utils.isEven(Nodes.size());
            Nodes.set(Nodes.size() - 1, p);
            System.out.println("replace last");
        }

        private void PrintNodes() {
            System.out.println("Nodes: ");
            for (Polar p : Nodes) {
                System.out.println("Dist:" + p.distance + " angle:" + p.angle + " deg:" + p.angle * 360 / 2 / Math.PI);
            }
        }

        void Finish() {
            if (insideSector && !Utils.isEven(Nodes.size())) {
                // terminate last sector
                final Polar last = Nodes.get(Nodes.size()-1);
                Visit(last.distance, last.angle);
            }

            assert (Utils.isEven(Nodes.size()));

            if (Nodes.size()>2 && insideSector && startedInsideSector.isPresent()) {
                // first sector is the end of last sector
                // combine them: remove two middle points
                assert Nodes.size()>=4;
                Nodes.set(0, Nodes.get(Nodes.size()-2));
                Nodes.get(0).angle -= 2 * Math.PI;
                Nodes.remove(Nodes.size()-1);
                Nodes.remove(Nodes.size()-1);
            }
        }

        PolarTurn GetBestRoute(Point robo, double robo_angle, Point goal) {
            // TODO goal is reacheable? FIXME
            assert !Nodes.isEmpty();
            final int dRoboGoal = Utils.GetDist(robo, goal);
            boolean localMin = true;
            int bestIdx = -1;
            int bestDist = Integer.MAX_VALUE;
            for (int i=0; i<Nodes.size(); ++i) {
                final Polar node = Nodes.get(i);

                final Point delta = Utils.PolarToDecart(node); // shift from current robot position
                final Point obstacleNode = new Point(robo.x + delta.x, robo.y + delta.y);
                final int dObstacleNodeToGoal = Utils.GetDist(obstacleNode, goal);
                if (dObstacleNodeToGoal < dRoboGoal) {
                    // node better than current position exists
                    localMin = false;
                }
                final int dist = Utils.GetDist(robo, obstacleNode) + dObstacleNodeToGoal;
                if (dist < bestDist) {
                    bestIdx = i;
                    bestDist = dist;
                }
            }

            if (localMin) {
                // local minimum detected, caller shuld switch to wal-following mode
                return null;
            }

            // Used later to calculate angle correction. See ToRobotMotion() function for details.
            // Even index means begin of obstacle sector, angle correction should be negative
            final int turnSign = Utils.isEven(bestIdx) ? -1 : 1;

            return new PolarTurn(Nodes.get(bestIdx), turnSign);
        }

        ArrayList<Polar> GetNodes() { return Nodes; } // used for test
    }

}
