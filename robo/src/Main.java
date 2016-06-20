package robo;

import ch.aplu.robotsim.*; //simulator uses jgamegrid library + robot classes (sensors, motor)
import ch.aplu.jgamegrid.*; //java library for creating games http://www.aplu.ch/home/apluhomex.jsp?site=75
import java.awt.Point;
import java.awt.Color;
import java.util.Date;
import robo.Interfaces.IRobot;

public class Main {

    // IStrategy implementation, which uses tangent bug algorithm
    static class Strategy_TangentBug implements Interfaces.IStrategy {
        final IRobot robot;
        final int goal_x;
        final int goal_y;

        Strategy_TangentBug(IRobot r, int goal_x, int goal_y) {
            robot = r;
            this.goal_x = goal_x;
            this.goal_y = goal_y;
        }

        // run tangent bug algorithm and return its result
        @Override
        public boolean run() {
            final Interfaces.IAlgorithm tb = new TangentBug();
            final boolean tbSuccess = tb.run(robot, goal_x, goal_y);
            if (tbSuccess) {
                System.out.println("TangentBug SUCCESS");
            } else {
                System.out.println("TangentBug FAILURE");
            }
            return tbSuccess;
        }
    }

    public static void main(String[] args) {
        try {
            //final IRobot r = new Simulator.Robot(250, 20); // U
            //final IRobot r = new Simulator.Robot(170, 20); // Plus
            final IRobot r = new Simulator.Robot(170, 20); // Square
            //final IRobot r = new Simulator.Robot(170,20); // T
            //final IRobot r = new Simulator.Robot(50,50); // Lab
            //final IRobot r = new Simulator.Robot(250,300); // Snail

            Tools.delay(1000); // pause

            final Date startDate = new Date();

            Interfaces.IStrategy strategy = new Strategy_TangentBug(r, goal_x, goal_y);
            boolean success = strategy.run();
            if (success) {
                System.out.println("IStrategy SUCCESS");
            } else {
                System.out.println("IStrategy FAILURE");
            }
            final Date endDate = new Date();

            // рассчитываем время прохождения препятствия
            final long diff = (endDate.getTime() - startDate.getTime()) / 1000;
            System.out.println("Time: "+diff+" seconds");
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }

    // add rectangle obstacle
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

        //T-shape
        if (false) {
            addObstacleBar(250, 120, 20, 250, Color.blue);
            addObstacleBar(120, 240, 150, 20, Color.blue);
        }

        //Lab1
        if (false) {
            addObstacleBar(0, 0, 20, 600, Color.CYAN);
            addObstacleBar(0, 0, 600, 20, Color.CYAN);
            addObstacleBar(480, 0, 20, 600, Color.CYAN);
            addObstacleBar(0, 480, 600, 20, Color.CYAN);
            addObstacleBar(20, 240, 300, 20, Color.CYAN);
            addObstacleBar(100, 0, 20, 150, Color.CYAN);
            addObstacleBar(100, 150, 200, 20, Color.CYAN);
            //addObstacleBar(250, 75, 200, 20, Color.CYAN);
            addObstacleBar(400, 150, 150, 20, Color.CYAN);
            addObstacleBar(200, 350, 400, 20, Color.CYAN);
        }

        // Snail
        if (false) {
            addObstacleBar(100, 100, 20, 250, Color.MAGENTA); //left
            addObstacleBar(100, 350, 200, 20, Color.MAGENTA); //нижняя
            addObstacleBar(300, 220, 20, 150, Color.MAGENTA);
            addObstacleBar(200, 220, 100, 20, Color.MAGENTA);
            addObstacleBar(100, 100, 350, 20, Color.MAGENTA);
            addObstacleBar(430, 100, 20, 300, Color.MAGENTA);
        }
    }

    static final int goal_x = 280;
    static final int goal_y = 430;

    // Goal is in the center (for sqare circle obstacle)
    //static final int goal_x = 250;
    //static final int goal_y = 250;

    //static final int goal_x = 200;
    //static final int goal_y = 430;

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
