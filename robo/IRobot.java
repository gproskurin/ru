package robo;

public interface IRobot {
    static public class Params {
        int RobotSize;
        int OneTurnTime;
    }

    Params get_params();

    void forward(int dist);
    void forward();

    void rotate(double angle);
    void rotate();

    void stop();

    int get_distance();

    int get_x();
    int get_y();

    double get_angle();
}
