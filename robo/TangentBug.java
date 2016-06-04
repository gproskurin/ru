package robo;

import java.awt.Point;
import robo.Utils.PolarTurn;

public class TangentBug implements IAlgorithm {
    static private final int NearObstacle = 70; //while moving forward - when robot see obstacle and estimated distance is less then 80, it stops
    static private final int NearGoal = 70; //to enlarge goal influence

    @Override
    public void run(IRobot r, int goal_x, int goal_y) {
        final Point goal = new Point(goal_x, goal_y);

        while (true) {
            if (RobotUtils.TillGoal(r, goal, NearObstacle, NearGoal)) {
                // goal reached
                return;
            }

            TangentGraph tg = null;
            while (true) {
                boolean afterWallFollow = false;
                if (!afterWallFollow)
                    tg = RobotUtils.ScanAround(r, goal_x, goal_y);
                final PolarTurn best = tg.GetBestRoute(new Point(r.get_x(), r.get_y()), r.get_angle(), goal);
                if (best == null) {
                    final Utils.FollowWallDirection fwd = tg.GetFollowWallDirection(
                            r.get_x(),
                            r.get_y(),
                            //r.get_angle(),
                            goal_x,
                            goal_y
                    );
                    FollowWall(r, fwd, tg.dReach, goal_x, goal_y);
                    tg = RobotUtils.ScanAround(r, goal_x, goal_y);
                    afterWallFollow = true;
                } else {
                    // move to the best node
                    System.out.println("Moving to best node...");
                    RobotUtils.MoveTo(r, best);
                    System.out.println(" - Moved");
                }
                final int goalDist = Utils.GetDist(r.get_x(), r.get_y(), goal.x, goal.y);
                if (tg.goalIsVisible(goalDist)) {
                    System.out.println("Goal is visible");
                    break;
                } else {
                    System.out.println("Goal is NOT visible");
                }
                tg = null;
            }
        }
    }

    private static void FollowWall(IRobot r, Utils.FollowWallDirection fwd, int dReach, int goal_x, int goal_y) {
        IAlgorithm wf = new WallFollower(fwd, dReach);
        wf.run(r, goal_x, goal_y);
    }

}
