package robo;

import ch.aplu.robotsim.*; //simulator uses jgamegridlibrary + robot classes (sensors, motor)
import ch.aplu.jgamegrid.*; //java library for creating games http://www.aplu.ch/home/apluhomex.jsp?site=75
import java.awt.*;


public class Robo {

    static class Robot {  //class Robot realises robots funcationality
        static final int OneTurnTime = 2190; //simulator has a lack - robot can't turn on certain angle. 2 secs - turnes around
        static final int RoboSize = 10; // safety interval from center of robot to obstacles. In order to take robot size into account
                                          //FOR correction of movement (for the function to correct movement)
        static private int Speed = -1; // units/second   units are coordinates X and Y on the screen.
                                        //Присваевается значение после выполнения функции calibrate speed

        public Gear gear;
        public UltrasonicSensor us;
        public LegoRobot robot;

        private double angle = 0;  //current angle of robot
        int getX() { return gear.getX(); }
        int getY() { return gear.getY(); }
        Point getPoint() { return new Point(getX(), getY()); }
        double getAngle() { return angle; }

        void gearRotate() { gear.right(); } //включает поворот робота в направлении увеличения угла

        // gets data from ultrosonic sensor/ returns -1 of no target in range or robot inside the target
        int GetDistance() {
            int d = us.getDistance();
            //return (d==255 ? -1 : d);  //for real robot
            return d;
        }

        // function to turn the robot on a certain angle
        void Rotate(double a) {
            a = Utils.NormalizeAngle(a);  //angle to turn
            angle = Utils.NormalizeAngle(angle + a);

            final int time = (int) ((double)OneTurnTime * Math.abs(a) / (2 * Math.PI));
            if (a > 0) {
                gear.right(time);  //time - на сколько включить функцию поворота
            } else {
                gear.left(time);
            }
            Tools.delay(time+100);
        }

        // move forward on a distance dist (dist in units, corresponds screen coordinates)
        void Forward(int dist) {  //just to switch on a motor
            assert Speed > 0; // must be calibrated, to bu sure not -1
            final int time = dist * 1000/Speed;
            gear.forward(time);
            Tools.delay(time+100);
        }

        private void CalibrateSpeed() {  //to distinguish robot's speed. Robot moves for one second, knowing current postion and finish we can calculate speed
            final int time = 1000;
            final Point begin = getPoint();  //current position of a robot
            gear.forward(time); //function forward for 1 second
            Tools.delay(time + 100); //delay . for sure
            final Point end = getPoint();
            final int dist = Utils.GetDist(begin, end);
            Speed = dist;  //сколько юнитов прошли за секунду.
        }

        // constructor. Robot is constructed and placed in the point with coordinates (x,y)
        Robot(int x, int y) {
            RobotContext.setStartPosition(x, y); //Robot.Context - simulator class with static functions for environmemt
            angle = 0;
            RobotContext.setStartDirection(0); //setStartDirect - which direction to orientate robot

            robot = new LegoRobot();

            gear = new Gear();
            robot.addPart(gear); //simulator function. initialize robot's components

            us = new UltrasonicSensor(SensorPort.S1); //s1 - means that sensor looks forward
            us.setBeamAreaColor(Color.green);
            us.setMeshTriangleColor(Color.blue);
            us.setProximityCircleColor(Color.lightGray);
            robot.addPart(us);

            CalibrateSpeed();
        }
    }

    public static void main(String[] args) {
        try {
            final Robot r = new Robot(100, 20);  // parameters - corrdinates  x,y
            Tools.delay(1000); //wait a second just to see how algorithm works (function from simulator)
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
        RobotContext.useObstacle(circle(5, Color.black), goal_x, goal_y); //draw a black small circle on a TARGET
    }

    private static GGBitmap circle(int radius, Color color) {
        GGBitmap bm = new GGBitmap(2 * radius, 2 * radius);
        bm.setPaintColor(color);
        bm.setLineWidth(3);
        bm.drawCircle(new Point(radius, radius), radius - 1);
        return bm;
    }


    //прямоугольник
    private static GGBitmap bar(int width, int length, Color color) {
        GGBitmap bm = new GGBitmap(width, length);
        bm.setPaintColor(color);
        bm.fillRectangle(new Point(0, 0), new Point(width - 1, length - 1));
        return bm;
    }
}
