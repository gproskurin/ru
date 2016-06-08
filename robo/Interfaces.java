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
            int RobotSize;
            int OneTurnTime;
        }

        Params get_params();

        // move forward for specified distance and stop
        void forward(int dist);

        // start moving forward
        void forward();

        // rotate for specified angle and stop
        void rotate(double angle);

        // start rotating
        void rotate();

        // stop rotating and moving
        void stop();

        // get distance to obstacle in front of the robot
        // return -1 if no obstacle
        int get_distance();

        // coordinates of robot
        int get_x();
        int get_y();

        double get_angle();
    }
}
