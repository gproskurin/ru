package robo;

public class Interfaces {

    public interface IStrategy {
        boolean run(); // returns true on success
    }


    public interface IAlgorithm {
        boolean run(IRobot r, int goal_x, int goal_y);// returns true on success
    }

    public interface IRobot {

        static public class Params {
            int RobotSize; // линейные размеры робота. Учитывается при коррекции движения
            int OneTurnTime; // время полного оборота вокруг своей оси. Используется для вращения на заданный угол
        }

        Params get_params();

        // move forward for specified distance and stop
        void forward(int dist);

        // start moving forward
        void forward();

        // rotate for specified angle and stop
        void rotate(double angle);

        // start rotating (until stop() is called)
        void rotate();

        // stop rotating and moving
        void stop();

        // get distance to obstacle in front of the robot
        // return -1 if no obstacle
        int get_distance();

        // coordinates of robot
        int get_x();
        int get_y();

        // current angle of robot relative to coordinate axis
        double get_angle();
    }
}
