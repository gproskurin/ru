package robo;

import ch.aplu.robotsim.*; //simulator uses jgamegridlibrary + robot classes (sensors, motor)
import ch.aplu.jgamegrid.*; //java library for creating games http://www.aplu.ch/home/apluhomex.jsp?site=75
import java.awt.Point;
import java.awt.Color;
import robo.Interfaces.IRobot;


public class Main {

    public static void main(String[] args) {
        try {
            final IRobot r = new Simulator.Robot(170, 20);  // parameters - corrdinates  x,y
            Tools.delay(1000); //wait a second just to see how algorithm works (function from simulator)
            Interfaces.IStrategy strategy = new Simulator.Strategy_TangentBug(r, goal_x, goal_y);
            strategy.run();
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }

    // (x,y) is upper left point
    // dx, dy are sizes
    private static void addObstacleBar(int x, int y, int dx, int dy, Color color) {
        if (!Utils.isEven(dx))
            ++dx;
        if (!Utils.isEven(dy))
            ++dy;
        final int half_x = dx / 2;
        final int half_y = dy / 2;
        final int center_x = x + half_x;
        final int center_y = y + half_y;

        Point mesh[] = {
            new Point(-half_x, -half_y),
            new Point(-half_x,  half_y),
            new Point( half_x,  half_y),
            new Point( half_x, -half_y)
        };
        RobotContext.useTarget(bar(dx, dy, color), mesh, center_x, center_y);
    }

    private static void addObstacles() {
        // U
        if (true) {
            addObstacleBar(130, 140, 40, 100, Color.blue);
            addObstacleBar(130, 230, 240, 40, Color.green);
            addObstacleBar(330, 70, 40, 160,Color.red);
        }

        // Plus
        if (false) {
            addObstacleBar(250, 120, 20, 280, Color.red);
            addObstacleBar(120, 240, 300, 20, Color.green);
        }

        // Square circle around obstacle
        if (false) {
            addObstacleBar(150, 150, 20, 200, Color.blue);
            addObstacleBar(150, 150, 200, 20, Color.yellow);
            addObstacleBar(350, 150, 20, 200, Color.green);
            addObstacleBar(150, 350, 220, 20, Color.cyan);
        }
    }

    static final int goal_x = 280;
    static final int goal_y = 430;

    // Goal is in the center (for sqare circle obstacle)
    //static final int goal_x = 250;
    //static final int goal_y = 250;

    static {
        addObstacles();
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
