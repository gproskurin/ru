package robo;

import java.awt.Point;
import robo.Interfaces.IAlgorithm;
import robo.Interfaces.IRobot;
import robo.Utils.PolarTurn;

public class TangentBug implements IAlgorithm {
    static private final int NearObstacle = 70; //while moving forward - when robot see obstacle and estimated distance is less then 80, it stops
    static private final int NearGoal = 50; //to enlarge goal influence

    @Override
    public boolean run(IRobot r, int goal_x, int goal_y) {
        final Point goal = new Point(goal_x, goal_y);

        while (true) {
            if (RobotUtils.TillGoal(r, goal, NearObstacle, NearGoal)) {
                // goal reached
                return true;
            }

            while (true) {
                final TangentGraph tg = RobotUtils.ScanAround(r, goal_x, goal_y);

                final int goalDist = Utils.GetDist(r.get_x(), r.get_y(), goal.x, goal.y);
                if (tg.goalIsVisible(goalDist)) {
                    System.out.println("Goal is visible");
                    break;
                }

                System.out.println("Goal is NOT visible");

                final PolarTurn best = tg.GetBestRoute(new Point(r.get_x(), r.get_y()), goal);
                if (best == null) {
                    // No best node, start following wall
                    final Utils.FollowWallDirection fwd = tg.GetFollowWallDirection(
                            r.get_x(),
                            r.get_y(),
                            //r.get_angle(),
                            goal_x,
                            goal_y
                    );
                    final boolean followWallSuccess = FollowWall(r, fwd, tg.dReach, goal_x, goal_y);
                    if (!followWallSuccess) {
                        System.out.println("FollowWall FAILURE (loop)");
                        return false; // loop detected
                    }
                    System.out.println("FollowWall SUCCESS");
                } else {
                    // move to the best node
                    System.out.println("Moving to best node: dist:"+best.polar.distance+" angle:"+best.polar.angle+" deg:"+Utils.rad2deg(best.polar.angle));
                    RobotUtils.MoveTo(r, best);
                    System.out.println(" - Moved");
                }
            }
        }
    }

    private static boolean FollowWall(IRobot r, Utils.FollowWallDirection fwd, int dReach, int goal_x, int goal_y) {
        IAlgorithm wf = new WallFollower(fwd, dReach);
        return wf.run(r, goal_x, goal_y);
    }
}
