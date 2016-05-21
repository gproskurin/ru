package robo;

import ch.aplu.robotsim.Tools;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Optional;
import robo.Utils.*;

public class TangentBug implements IAlgorithm {
    static private final int NearObstacle = 70; //while moving forward - when robot see obstacle and estimated distance is less then 80, it stops
    static private final int NearGoal = 50; //to enlarge goal influence

    @Override
    public void run(IRobot r, int goal_x, int goal_y) {
        final Point goal = new Point(goal_x, goal_y);

        while (true) {
            if (TillGoal(r, goal)) {
                // goal reached
                return;
            }

            while (true) {
                final double goalAbsoluteAngle = Utils.ComputeAngle(r.get_x(), r.get_y(), goal_x, goal_y); //what is current angle
                final TangentGraph tg = ScanAround(r, goalAbsoluteAngle); //absoluteAngle - not dependent on robots angle
                final PolarTurn best = tg.GetBestRoute(new Point(r.get_x(), r.get_y()), r.get_angle(), goal);
                if (best == null) {
                    final Utils.FollowWallDirection fwd = tg.GetFollowWallDirection(r.get_x(), r.get_y(), goal_x, goal_y);
                    System.out.println("FollowWall: wall_on_right?"+fwd.WallOnTheRight+" angle:"+fwd.angle);
                    FollowWall(r, fwd, goal_x, goal_y);
                } else {
                    // move to the best node
                    System.out.println("Moving to best node...");
                    MoveTo(r, best);
                    System.out.println(" - Moved");
                }
                final int goalDist = Utils.GetDist(r.get_x(), r.get_y(), goal.x, goal.y);
                if (tg.goalIsVisible(goalDist)) {
                    System.out.println("Goal is visible");
                    break;
                }
            }
        }
    }

    // correction taking into account robot size
    static private void MoveTo(IRobot r, PolarTurn node) {
        final Polar motion = Utils.ToRobotMotion(node, r.get_params().RobotSize); //
        final double deltaAngle = motion.angle - r.get_angle(); // convert from absolute angle to robot's POV angle
        r.rotate(deltaAngle);
        r.forward(motion.distance);
    }

    static private boolean GoalReached(IRobot r, final Point goal) {
        return Utils.GetDist(r.get_x(),r.get_y(), goal.x, goal.y) <= NearGoal;
    }

    // return true if goal reached, false if obstacle occured
    private static boolean TillGoal(IRobot r, final Point goal)
    {
        System.out.println("TillGoal...");
        final double angle = Utils.ComputeAngle(r.get_x(), r.get_y(), goal.x, goal.y); //find a direction where to turn. angle from robot to goal
        r.rotate(angle - r.get_angle());
        r.forward();
        while (true) {
            if (GoalReached(r, goal)) {
                r.stop();
                System.out.println(" - TillGoal: goal reached");
                return true;
            }
            final int d = r.get_distance();
            if (d >= 0 && d <= NearObstacle) { //we reached an obstacle
                r.stop();
                System.out.println(" - TillGoal: obstacle");
                return false;
            }
            Tools.delay(10);  //small delay to relax
        }
    }

    // scan around and create tangent graph
    // while scanning, save distance in goal's direction
    private static TangentGraph ScanAround(IRobot r, double goalAbsoluteAngle) {
        System.out.println("ScanAround: start...");
        TangentGraph tg = new TangentBug.TangentGraph(goalAbsoluteAngle);

        final double robot_angle = r.get_angle();

        tg.AddSensorSample(r.get_distance(), robot_angle);

        Tools.startTimer(); //needed to calculate current turn angle
        r.rotate();

        while (Tools.getTime() < r.get_params().OneTurnTime) {
            int d = r.get_distance(); //distance to the nearest obstacle
            final double robotPovAngle = 2 * Math.PI * Tools.getTime() / r.get_params().OneTurnTime;  //current angle of robot
            final double absoluteAngle = robotPovAngle + robot_angle;
            tg.AddSensorSample(d, absoluteAngle);
            Tools.delay(10); //100 раз в секунду снятие показаний
        }
        r.stop();
        //tg.Visit(r.GetDistance(), 360);
        tg.Finish();

        System.out.println(" - ScanAround: end");
        Tools.delay(1000); //
        return tg;
    }

    private static void FollowWall(IRobot r, Utils.FollowWallDirection fwd, int goal_x, int goal_y) {
        IAlgorithm wf = new WallFollower(fwd);
        wf.run(r, goal_x, goal_y);
    }

    static class TangentGraph {

        private final ArrayList<Polar> Nodes = new ArrayList<>();
        private Optional<Boolean> startedInsideSector = Optional.empty();
        private boolean insideSector = false;

        private Polar goalSample = null; //to record the direction and distance to GOAL
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

        // searches tangent graph. returns null if local minima
        PolarTurn GetBestRoute(Point robo, double robo_angle, Point goal) {
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

        Utils.FollowWallDirection GetFollowWallDirection(int robo_x, int robo_y, int goal_x, int goal_y) {
            if (Nodes.isEmpty())
                return null;
            assert Nodes.size() >= 2;
            final Polar p1 = Nodes.get(0);
            final Polar p2 = Nodes.get(1);
            final Point p1_dec = Utils.DecartFromPoint(robo_x, robo_y, p1);
            final Point p2_dec = Utils.DecartFromPoint(robo_x, robo_y, p2);
            final int dist_goal_1 = Utils.GetDist(goal_x, goal_y, p1_dec);
            final int dist_goal_2 = Utils.GetDist(goal_x, goal_y, p2_dec);
            final boolean wallOnTheRight = dist_goal_1 > dist_goal_2;
            double sector_angle = Utils.ComputeAngle(p1_dec.x, p1_dec.y, p2_dec.x, p2_dec.y);
            if (!wallOnTheRight)
                sector_angle += Math.PI;
            return new FollowWallDirection(wallOnTheRight, sector_angle);
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
