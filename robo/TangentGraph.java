package robo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Optional;

public class TangentGraph {
    private static int dDist = 20;
    private final ArrayList<Utils.Polar> Nodes = new ArrayList<>();
    private Optional<Boolean> startedInsideSector = Optional.empty();
    private boolean insideSector = false;

    private Utils.Polar goalSample = null; //to record the direction and distance to GOAL
    private final double goalAngle;

    final int robo_x;
    final int robo_y;
    final int goal_x;
    final int goal_y;
    int dReach = Integer.MAX_VALUE;

    TangentGraph(int robo_x, int robo_y, int goal_x, int goal_y) {
        this.robo_x = robo_x;
        this.robo_y = robo_y;
        this.goal_x = goal_x;
        this.goal_y = goal_y;
        goalAngle = Utils.ComputeAngle(robo_x, robo_y, goal_x, goal_y);
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
                Nodes.add(Nodes.get(Nodes.size() - 1));
            }
            assert Utils.isEven(Nodes.size());
            insideSector = false;
            return;
        }

        final Utils.Polar polar = new Utils.Polar(dist, absoluteAngle);

        // calculate dReach
        dReach = Math.min(
                dReach,
                Utils.GetDist(
                        goal_x,
                        goal_y,
                        Utils.DecartFromPoint(robo_x, robo_y, polar)
                )
        );

        if (!insideSector || !Utils.isEven(Nodes.size())) {
            // begin of new sector OR second point of last sector
            // just append it
            //assert Utils.isEven(Nodes.size());
            Nodes.add(polar);
            insideSector = true;
            return;
        }

        // third point of current sector
        assert !Nodes.isEmpty();
        assert Utils.isEven(Nodes.size());

        final Utils.Polar last = Nodes.get(Nodes.size() - 1);

        if (Math.abs(last.distance - polar.distance) < dDist) {
            // replace last point
            Nodes.set(Nodes.size() - 1, polar);
        } else {
            // overlapped obstacles detected
            // store equal angle for adjacent samples as indication for overlapped obstacles
            final double new_angle = (last.angle + polar.angle) / 2;
            last.angle = new_angle;
            polar.angle = new_angle;
            Nodes.add(polar);
            assert insideSector;
        }
    }

    void Finish() {
        if (insideSector && !Utils.isEven(Nodes.size())) {
            // terminate last sector
            final Utils.Polar last = Nodes.get(Nodes.size() - 1);
            AddSensorSample(last.distance, last.angle);
        }

        assert (Utils.isEven(Nodes.size()));

        if (Nodes.size() > 2 && insideSector && startedInsideSector.isPresent() && startedInsideSector.get()) {
            // first sector is the end of last sector
            // combine them: remove two middle points
            assert Nodes.size() >= 4;
            Nodes.set(0, Nodes.get(Nodes.size() - 2));
            Nodes.get(0).angle -= 2 * Math.PI;
            Nodes.remove(Nodes.size() - 1);
            Nodes.remove(Nodes.size() - 1);
        }
        PrintNodes();
    }

    // searches tangent graph. returns null if local minima
    Utils.PolarTurn GetBestRoute(Point robo, Point goal) {
        assert !Nodes.isEmpty();
        final int dRoboGoal = Utils.GetDist(robo, goal);
        boolean localMin = true;
        int bestIdx = -1;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < Nodes.size(); ++i) {
            final Utils.Polar node = Nodes.get(i);

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

        // Test, whether best point has overlapping neighbour
        final int nearIdx = (Utils.isEven(bestIdx) ? bestIdx - 1 : bestIdx + 1);
        if (nearIdx >= 0 && nearIdx < Nodes.size()) {
            final Utils.Polar nearPolar = Nodes.get(nearIdx);
            final Utils.Polar bestPolar = Nodes.get(bestIdx);
            final boolean overlap = (nearPolar.angle == bestPolar.angle);
            if (overlap && bestPolar.distance > nearPolar.distance) {
                // If best node has overlapping neighbour and more far then this neighbour,
                // move to closer neighbour instead
                bestIdx = nearIdx;
            }
        }

        // Used later to calculate angle correction. See ToRobotMotion() function for details.
        // Even index means begin of obstacle sector, angle correction should be negative
        // Odd index means end of obstacle sector, angle correction should be positive
        final int turnSign = Utils.isEven(bestIdx) ? -1 : 1;

        final Utils.PolarTurn turn = new Utils.PolarTurn(Nodes.get(bestIdx), turnSign);
        return turn;
    }

    Utils.FollowWallDirection GetFollowWallDirection(int robo_x, int robo_y, int goal_x, int goal_y) {
        if (Nodes.isEmpty()) {
            return null;
        }
        assert Nodes.size() >= 2;
        final Utils.Polar p1 = Nodes.get(0);
        final Utils.Polar p2 = Nodes.get(1);
        final Point p1_dec = Utils.DecartFromPoint(robo_x, robo_y, p1);
        final Point p2_dec = Utils.DecartFromPoint(robo_x, robo_y, p2);
        final int dist_goal_1 = Utils.GetDist(goal_x, goal_y, p1_dec);
        final int dist_goal_2 = Utils.GetDist(goal_x, goal_y, p2_dec);
        final boolean wallOnTheRight = dist_goal_1 > dist_goal_2;
        double sector_angle = Utils.ComputeAngle(p1_dec.x, p1_dec.y, p2_dec.x, p2_dec.y);
        if (!wallOnTheRight) {
            sector_angle += Math.PI;
        }
        return new Utils.FollowWallDirection(wallOnTheRight, sector_angle);
    }

    private static Utils.Polar getBestAngleApprox(Utils.Polar currentBest, double goalAngle, int sampleDist, double sampleAngle) {
        if (currentBest == null || (Math.abs(goalAngle - sampleAngle) < Math.abs(currentBest.angle - goalAngle))) {
            return new Utils.Polar(sampleDist, sampleAngle);
        } else {
            return currentBest;
        }
    }

    int GetGoalDist() {
        return goalSample.distance;
    }

    boolean goalIsVisible(int goalDist) {
        assert goalSample != null;
        assert Utils.isEven(Nodes.size());
        for (int i = 0; i < Nodes.size(); i += 2) {
            final Utils.Polar begin = Nodes.get(i);
            final Utils.Polar end = Nodes.get(i + 1);
            assert begin.angle <= end.angle;
            if (begin.angle <= goalSample.angle && goalSample.angle <= end.angle) {
                final boolean visible = (goalSample.distance >= goalDist);
                if (visible)
                    System.out.println("visible: "+begin.angle+" <= "+goalSample.angle+" <= "+end.angle);
                return visible;
            }
        }
        return true;
    }

    private void PrintNodes() {
        System.out.println("Nodes: ");
        for (Utils.Polar p : Nodes) {
            System.out.println("Dist:" + p.distance + " angle:" + p.angle + " deg:" + Utils.rad2deg(p.angle));
        }
    }

    // used for test
    ArrayList<Utils.Polar> GetNodes() {
        /*PrintNodes();*/
        return Nodes;
    }
}
