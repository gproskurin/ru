package robo;

import ch.aplu.robotsim.*;
import java.awt.Color;
import robo.Interfaces.IRobot;
import robo.Interfaces.IStrategy;
import robo.Interfaces.IAlgorithm;

public class Simulator {

    static class Strategy_TangentBug implements IStrategy {
        final IRobot robot;
        final int goal_x;
        final int goal_y;

        Strategy_TangentBug(IRobot r, int goal_x, int goal_y) {
            robot = r;
            this.goal_x = goal_x;
            this.goal_y = goal_y;
        }

        @Override
        public void run() {
            final IAlgorithm tb = new TangentBug();
            tb.run(robot, goal_x, goal_y);
        }
    }

    
    static class Robot implements IRobot {
        private IRobot.Params params;

        private void init_params() {
            params = new IRobot.Params();
            params.RobotSize = 10; // safety interval from center of robot to obstacles. In order to take robot size into account
            //FOR correction of movement (for the function to correct movement)
            params.OneTurnTime = 2180; //simulator has a lack - robot can't turn on certain angle. 2 secs - turnes around
        }

        @Override
        public IRobot.Params get_params() {
            return params;
        }
        private static int Speed = -1; // units/second   units are coordinates X and Y on the screen.
        //Присваевается значение после выполнения функции calibrate speed

        private Gear gear;
        private UltrasonicSensor sensor;
        private LegoRobot robot;

        private double angle = 0;  //current angle of robot

        @Override
        public int get_x() {
            return gear.getX();
        }

        @Override
        public int get_y() {
            return gear.getY();
        }

        @Override
        public double get_angle() {
            return angle;
        }

        @Override
        public void rotate() {
            gear.right();
        } //включает поворот робота в направлении увеличения угла

        // gets data from ultrosonic sensor/ returns -1 of no target in range or robot inside the target
        @Override
        public int get_distance() {
            int d = sensor.getDistance();
            //return (d==255 ? -1 : d);  //for real robot
            return d;
        }

        // function to turn the robot on a certain angle
        @Override
        public void rotate(double a) {
            a = Utils.NormalizeAngle(a);  //angle to turn
            angle = Utils.NormalizeAngle(angle + a);

            final int time = (int) ((double) params.OneTurnTime * Math.abs(a) / (2 * Math.PI));
            if (a > 0) {
                gear.right(time);  //time - на сколько включить функцию поворота
            } else {
                gear.left(time);
            }
            Tools.delay(time + 100);
        }

        @Override
        public void stop() {
            gear.stop();
        }

        // move forward on a distance dist (dist in units, corresponds screen coordinates)
        @Override
        public void forward(int dist) {  //just to switch on a motor
            assert Speed > 0; // must be calibrated, to bu sure not -1
            final int time = dist * 1000 / Speed;
            gear.forward(time);
            //Tools.delay(time+100);
        }

        @Override
        public void forward() {
            gear.forward();
        }

        private void CalibrateSpeed() {  //to distinguish robot's speed. Robot moves for one second, knowing current postion and finish we can calculate speed
            final int time = 1000;

            // current position of a robot
            final int begin_x = gear.getX();
            final int begin_y = gear.getY();

            gear.forward(time); //function forward for 1 second
            //Tools.delay(time + 100); //delay . for sure

            //
            final int end_x = gear.getX();
            final int end_y = gear.getY();

            final int dist = Utils.GetDist(begin_x, begin_y, end_x, end_y);
            Speed = dist;  //сколько юнитов прошли за секунду.
        }

        // constructor. Robot is constructed and placed in the point with coordinates (x,y)
        Robot(int x, int y) {
            init_params();

            RobotContext.setStartPosition(x, y); //Robot.Context - simulator class with static functions for environmemt
            angle = 0;
            RobotContext.setStartDirection(0); //setStartDirect - which direction to orientate robot

            robot = new LegoRobot();

            gear = new Gear();
            robot.addPart(gear); //simulator function. initialize robot's components

            sensor = new UltrasonicSensor(SensorPort.S1); //s1 - means that sensor looks forward
            sensor.setBeamAreaColor(Color.green);
            sensor.setMeshTriangleColor(Color.blue);
            sensor.setProximityCircleColor(Color.lightGray);
            robot.addPart(sensor);

            System.out.println("Calibrating speed...");
            CalibrateSpeed();
            System.out.println(" - Calibrated, speed = " + Speed + " units/second");
        }
    }
}
