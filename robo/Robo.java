package robo;

import ch.aplu.robotsim.*;
import java.awt.*;
import ch.aplu.jgamegrid.*;

public class Robo {

    static class Robot {
        static final int OneTurnTime = 2190;
        static final int RoboSize = 10; // safety interval fron center of robot to abstacles
        static private int Speed = -1; // units/second

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
            a = Utils.NormalizeAngle(a);
            angle = Utils.NormalizeAngle(angle + a);

            final int time = (int) ((double)OneTurnTime * Math.abs(a) / (2 * Math.PI));
            if (a > 0) {
                gear.right(time);
            } else {
                gear.left(time);
            }
            Tools.delay(time+100);
        }

        void Forward(int dist) {
            assert Speed > 0; // must be calibrated
            final int time = dist * 1000/Speed;
            gear.forward(time);
            Tools.delay(time+100);
        }

        private void CalibrateSpeed() {
            final int time = 1000;
            final Point begin = getPoint();
            gear.forward(time);
            Tools.delay(time + 100);
            gear.stop();
            final Point end = getPoint();
            final int dist = Utils.GetDist(begin, end);
            Speed = dist;
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

            CalibrateSpeed();
        }
    }

    public static void main(String[] args) {
        try {
            final Robot r = new Robot(100, 20);
            Tools.delay(1000);
            final Point goal = new Point(goal_x, goal_y);
            TangentBug.Run(r, goal);
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
