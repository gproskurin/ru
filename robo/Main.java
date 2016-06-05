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

    private static void addObstacles() {
        Point mesh1[] = {
            new Point(-20, -50),
            new Point(-20, 50),
            new Point(20, 50),
            new Point(20, -50)
        };
        RobotContext.useTarget(bar(40, 100, Color.blue), mesh1, 150, 190);

        Point mesh2[] = {
            new Point(-120, -20),
            new Point(-120, 20),
            new Point(120, 20),
            new Point(120, -20)
        };
        RobotContext.useTarget(bar(240, 40, Color.green), mesh2, 250, 250);

        Point mesh3[] = {
            new Point(-20, -80),
            new Point(-20, 80),
            new Point(20, 80),
            new Point(20, -80)
        };
        RobotContext.useTarget(bar(40, 160, Color.red), mesh3, 350, 150);

        Point mesh4[] = {
            new Point(-20, -20),
            new Point(-20, 20),
            new Point(20, 20),
            new Point(20, -20)
        };
        RobotContext.useTarget(bar(40, 40, Color.cyan), mesh4, 190, 390);
    }

    static final int goal_x = 280;
    static final int goal_y = 430;
    //static final int goal_x = 450;
    //static final int goal_y = 450;

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
