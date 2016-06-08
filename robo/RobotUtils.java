package robo;

import ch.aplu.robotsim.Tools;
import java.awt.Point;
import robo.Interfaces.IRobot;

public class RobotUtils {

    // scan around and create tangent graph
    // while scanning, save distance in goal's direction
    static TangentGraph ScanAround(IRobot r, int goal_x, int goal_y) {
        System.out.println("ScanAround: start...");
        TangentGraph tg = new TangentGraph(r.get_x(), r.get_y(), goal_x, goal_y);

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
        tg.Finish();

        System.out.println(" - ScanAround: end");
        Tools.delay(1000);
        return tg;
    }

    // correction taking into account robot size
    static void MoveTo(IRobot r, Utils.PolarTurn node) {
        final Utils.Polar motion = Utils.ToRobotMotion(node, r.get_params().RobotSize); //
        final double deltaAngle = motion.angle - r.get_angle(); // convert from absolute angle to robot's POV angle
        r.rotate(deltaAngle);
        r.forward(motion.distance);
    }

    // return true if goal reached, false if obstacle occured
    static boolean TillGoal(IRobot r, final Point goal, int nearObstacleDist, int nearGoalDist)
    {
        System.out.println("TillGoal...");
        final double angle = Utils.ComputeAngle(r.get_x(), r.get_y(), goal.x, goal.y); //find a direction where to turn. angle from robot to goal
        r.rotate(angle - r.get_angle());
        r.forward();
        while (true) {
            if (PointReached(r, goal, nearGoalDist)) {
                r.stop();
                System.out.println(" - TillGoal: goal reached");
                return true;
            }
            final int d = r.get_distance();
            if (d >= 0 && d <= nearObstacleDist) { //we reached an obstacle
                r.stop();
                System.out.println(" - TillGoal: obstacle");
                return false;
            }
            Tools.delay(10);  //small delay to relax
        }
    }

    static boolean PointReached(IRobot r, final Point goal, int nearDist) {
        return Utils.GetDist(r.get_x(),r.get_y(), goal.x, goal.y) <= nearDist;
    }
}
