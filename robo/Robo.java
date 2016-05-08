package robo;

import ch.aplu.robotsim.*;
//import ch.aplu.util.*;
import java.awt.*;
import ch.aplu.jgamegrid.*;
//import ch.aplu.ev3.*;
import robo.Utils.*;
//import static robo.Utils.NormalizeAngle;


public class Robo {

    static class Robot {
        public static final int OneTurnTime = 2190;
        static final int RoboSize = 10; // safety interval fron center of robot to abstacles
        public Gear gear;
        public UltrasonicSensor us;
        public LegoRobot robot;

        private double angle = 0;
        int getX() { return gear.getX(); }
        int getY() { return gear.getY(); }
        Point getPoint() { return new Point(getX(), getY()); }
        double getAngle() { return angle; }

        void gearRotate() { gear.right(); }

        int GetDistance() {
            int d = us.getDistance();
            //return (d==255 ? -1 : d);
            return d;
        }

        void Rotate(double a) {
            //System.out.println("RL:"+a);
            a = Utils.NormalizeAngle(a);

            //if (Math.abs(a) < 0.01)
            //    return;

            angle = Utils.NormalizeAngle(angle + a);

            final int time = (int) ((double)OneTurnTime * Math.abs(a) / (2 * Math.PI));
            if (a > 0) {
                gear.right(time);
            } else {
                gear.left(time);
            }
            Tools.delay(time+100);
        }

        Robot(int x, int y) {
            RobotContext.setStartPosition(x, y);
            angle = 0;
            RobotContext.setStartDirection(0);

            robot = new LegoRobot();

            gear = new Gear();
            robot.addPart(gear);

            us = new UltrasonicSensor(SensorPort.S1);
            us.setBeamAreaColor(Color.green);
            us.setMeshTriangleColor(Color.blue);
            us.setProximityCircleColor(Color.lightGray);
            robot.addPart(us);
        }
    }

    static TangentBug.TangentGraph ScanAround(Robot r)
    {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph();

        final double robot_angle = r.getAngle();

        Tools.startTimer();
        r.gearRotate();

        tg.Visit(r.GetDistance(), robot_angle);
        while (Tools.getTime() < Robot.OneTurnTime) {
            int d = r.GetDistance();
            final double robotPovAngle = 2 * Math.PI * Tools.getTime() / Robot.OneTurnTime;
            final double absoluteAngle = robotPovAngle + robot_angle;
            tg.Visit(d, absoluteAngle);
            Tools.delay(10);
        }
        r.gear.stop();
        Tools.delay(1000);
        //tg.Visit(r.GetDistance(), 360);
        tg.Finish();

        return tg;
    }

    static void TillObstacle(Robot r, int dist)
    {
        while (true) {
            final int d = r.us.getDistance();
            if (d >= 0 && d <= dist) {
                return;
            }
            Tools.delay(10);
        }
    }

    static void RunRobot(Robot r) {
        r.gear.forward();
        TillObstacle(r, 80);
        r.gear.stop();
        //Tools.delay(1000);
        TangentBug.TangentGraph tg = ScanAround(r);
        final PolarTurn best = tg.GetBestRoute(r.getPoint(), r.getAngle(), new Point(goal_x,goal_y));
        if (best == null) {
            TangentBug.FollowWall();
            // TODO
        }
        final Polar motion = Utils.ToRobotMotion(best, Robot.RoboSize);

        final double deltaAngle = motion.angle - r.getAngle(); // convert from absolute angle to robot's POV angle
        r.Rotate(deltaAngle);
        r.gear.forward();

        Tools.delay(5000);

        r.robot.exit();
    }

    public static void main(String[] args) {
        try {
            Robot r = new Robot(100, 20);
            Tools.delay(1000);
            r.Rotate(Math.PI/3);
            Tools.delay(2000);
            RunRobot(r);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }

    private static void addObstacles() {
        Point mesh1[] = {
            new Point(-100, -50),
            new Point(-100, 50),
            new Point(100, 50),
            new Point(100, -50)
        };
        RobotContext.useTarget(bar(200, 100, Color.red), mesh1, 250, 250);

        Point mesh2[] = {
            new Point(-20, -20),
            new Point(-20, 20),
            new Point(20, 20),
            new Point(20, -20)
        };
        RobotContext.useTarget(bar(40, 40, Color.red), mesh2, 400, 50);
    }

    //static final int goal_x = 200;
    //static final int goal_y = 490;
    static final int goal_x = 400;
    static final int goal_y = 300;

    static {
        //RobotContext.showNavigationBar();

        addObstacles();

        //RobotContext.useObstacle(bar(200, 100, Color.red), 250, 250);
        //RobotContext.useTarget(bar(200, 100, Color.red), 250, 250);
        //RobotContext.useObstacle(bar(300, 20, Color.green), 250, 350);
        //RobotContext.useObstacle(bar(20, 300, Color.blue), 150, 250);
        //RobotContext.useObstacle(bar(20, 300, Color.yellow), 350, 250);
        RobotContext.useObstacle(circle(5, Color.black), goal_x, goal_y);
    }

    private static GGBitmap circle(int radius, Color color) {
        GGBitmap bm = new GGBitmap(2 * radius, 2 * radius);
        bm.setPaintColor(color);
        bm.setLineWidth(3);
        bm.drawCircle(new Point(radius, radius), radius - 1);
        return bm;
    }

    private static GGBitmap bar(int width, int length, Color color) {
        GGBitmap bm = new GGBitmap(width, length);
        bm.setPaintColor(color);
        bm.fillRectangle(new Point(0, 0), new Point(width - 1, length - 1));
        return bm;
    }
}
