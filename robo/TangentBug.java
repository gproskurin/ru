package robo;

import ch.aplu.robotsim.Tools;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Optional;
import robo.Utils.*;

public class TangentBug {
    static private final int NearObstacle = 80;
    static private final int NearGoal = 50;

    static void Run(Robo.Robot r, final Point goal) {

        while (true) {
            if (TillGoal(r, goal)) {
                // goal reached
                return;
            }

            while (true) {
                final double goalAbsoluteAngle = Utils.ComputeAngle(r.getPoint(), goal);
                final TangentGraph tg = ScanAround(r, goalAbsoluteAngle);
                final PolarTurn best = tg.GetBestRoute(r.getPoint(), r.getAngle(), goal);
                if (best == null) {
                    // TODO
                    FollowWall(r);
                } else {
                    // move to best node
                    MoveTo(r, best);
                }
                final int goalDist = Utils.GetDist(r.getPoint(), goal);
                if (tg.goalIsVisible(goalDist)) {
                    break;
                }
            }
        }
    }

    static private void MoveTo(Robo.Robot r, PolarTurn node) {
        final Polar motion = Utils.ToRobotMotion(node, Robo.Robot.RoboSize);
        final double deltaAngle = motion.angle - r.getAngle(); // convert from absolute angle to robot's POV angle
        r.Rotate(deltaAngle);
        r.Forward(motion.distance);
    }

    static private boolean GoalReached(Robo.Robot r, final Point goal) {
        return Utils.GetDist(r.getPoint(), goal) <= NearGoal;
    }

    // return true if goal reached, false if obstacle occured
    private static boolean TillGoal(Robo.Robot r, final Point goal)
    {
        final double angle = Utils.ComputeAngle(r.getPoint(), goal);
        r.Rotate(angle);
        r.gear.forward();
        while (true) {
            if (GoalReached(r, goal)) {
                r.gear.stop();
                return true;
            }
            final int d = r.us.getDistance();
            if (d >= 0 && d <= NearObstacle) {
                r.gear.stop();
                return false;
            }
            Tools.delay(10);
        }
    }

    // scan around and create tangent graph
    // while scanning, save distance in goal's direction
    private static TangentGraph ScanAround(Robo.Robot r, double goalAbsoluteAngle) {
        TangentGraph tg = new TangentBug.TangentGraph(goalAbsoluteAngle);

        final double robot_angle = r.getAngle();

        tg.AddSensorSample(r.GetDistance(), robot_angle);

        Tools.startTimer();
        r.gearRotate();

        while (Tools.getTime() < Robo.Robot.OneTurnTime) {
            int d = r.GetDistance();
            final double robotPovAngle = 2 * Math.PI * Tools.getTime() / Robo.Robot.OneTurnTime;
            final double absoluteAngle = robotPovAngle + robot_angle;
            tg.AddSensorSample(d, absoluteAngle);
            Tools.delay(10);
        }
        r.gear.stop();
        Tools.delay(1000);
        //tg.Visit(r.GetDistance(), 360);
        tg.Finish();

        return tg;
    }

    private static void FollowWall(Robo.Robot r) {
        // TODO
        r.gearRotate();
    }

    static class TangentGraph {

        private final ArrayList<Polar> Nodes = new ArrayList<>();
        private Optional<Boolean> startedInsideSector = Optional.empty();
        private boolean insideSector = false;

        private Polar goalSample = null;
        private final double goalAngle;

        TangentGraph(double goalAbsoluteAngle) {
            goalAngle = goalAbsoluteAngle;
        }

        void AddSensorSample(int dist, double absoluteAngle) {
            goalSample = getBestAngleApprox(goalSample, goalAngle, dist, absoluteAngle);

            if (!startedInsideSector.isPresent()) {
                assert Nodes.isEmpty();
                startedInsideSector = Optional.of(dist >= 0);
            }

            if (!insideSector) {
                assert (Utils.isEven(Nodes.size()));
            }

            if (dist < 0) {
                // end of current sector (if any)
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
                Nodes.add(p);
                insideSector = true;
                return;
            }

            // third point of current sector, replace last point
            assert !Nodes.isEmpty();
            assert Utils.isEven(Nodes.size());
            Nodes.set(Nodes.size() - 1, p);
        }

        void Finish() {
            if (insideSector && !Utils.isEven(Nodes.size())) {
                // terminate last sector
                final Polar last = Nodes.get(Nodes.size()-1);
                AddSensorSample(last.distance, last.angle);
            }

            assert (Utils.isEven(Nodes.size()));

            if (Nodes.size()>2 && insideSector && startedInsideSector.isPresent() && startedInsideSector.get()) {
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

        private static Polar getBestAngleApprox(Polar currentBest, double goalAngle, int sampleDist, double sampleAngle) {
            if (currentBest == null || (Math.abs(goalAngle - sampleAngle) < Math.abs(currentBest.angle - goalAngle))) {
                return new Polar(sampleDist, sampleAngle);
            } else {
                return currentBest;
            }
        }

        boolean goalIsVisible(int goalDist) {
            assert goalSample != null;
            assert Utils.isEven(Nodes.size());
            for (int i=0; i<Nodes.size(); i+=2) {
                final Polar begin = Nodes.get(i);
                final Polar end = Nodes.get(i+1);
                assert begin.angle <= end.angle;
                if (begin.angle <= goalSample.angle && goalSample.angle <= end.angle) {
                    return (goalSample.distance >= goalDist);
                }
            }
            return true;
        }

        /*
        private void PrintNodes() {
            System.out.println("Nodes: ");
            for (Polar p : Nodes) {
                System.out.println("Dist:" + p.distance + " angle:" + p.angle + " deg:" + p.angle * 360 / 2 / Math.PI);
            }
        }
        */

        ArrayList<Polar> GetNodes() { /*PrintNodes();*/return Nodes; } // used for test
    }

}
