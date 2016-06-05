package robo;

public class Interfaces {

    public interface IStrategy {
        void run();
    }


    public interface IAlgorithm {
        void run(IRobot r, int goal_x, int goal_y);
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

        int get_distance();

        int get_x();

        int get_y();

        double get_angle();
    }
}
