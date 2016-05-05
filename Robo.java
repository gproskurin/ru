package robo;

import ch.aplu.robotsim.*;
//import ch.aplu.util.*;
import java.awt.*;
import ch.aplu.jgamegrid.*;
import java.util.ArrayList;
//import ch.aplu.ev3.*;


public class Robo {
    
    static class TangentBug {
        static class Polar {
            Polar (int d, double a) { distance = d; angle = a; }
            int distance;
            double angle;
        }
        
        static class TangentGraph {
            private static boolean MiddleIsUseless(Polar p1, Polar p2, Polar p3) {
                final int sig1 = Integer.signum(p1.distance);
                final int sig2 = Integer.signum(p2.distance);
                final int sig3 = Integer.signum(p3.distance);
                return sig1==sig2 && sig2==sig3;
            }
            
            void Visit(int dist, double angle) {
                final Polar p = new Polar(dist, angle);
                if (Points.isEmpty()) {
                    Points.add(p);
                    return;
                }
                if (Points.size() >= 2 && MiddleIsUseless(Points.get(Points.size() - 2), Points.get(Points.size() - 1), p)) {
                    Points.set(Points.size() - 1, p);
                    return;
                }
                // add point
                Points.add(p);
            }
            
            void Finish() {
                if (Points.size() >= 2 && MiddleIsUseless(Points.get(Points.size() - 2), Points.get(Points.size() - 1), Points.get(0))) {
                    Points.remove(Points.size()-1);
                }
                if (Points.size() >= 2 && MiddleIsUseless(Points.get(Points.size() - 1), Points.get(0), Points.get(1))) {
                    Points.remove(0);
                }
                for (Polar p : Points) {
                    System.out.println("Dist:"+p.distance+" angle:"+p.angle+" deg:"+p.angle*360/2/Math.PI);
                }
            }
            
            private int DistXY(int x1, int y1, int x2, int y2) {
                return (int)Math.sqrt((x1-x2)^2 + (y1-y2)^2);
            }
            
            Polar GetBestRoute(int robo_x, int robo_y, double robo_angle, int goal_x, int goal_y) {
                return Points.get(0);
                //if (Points.isEmpty())
                //    return goal;
                Polar best = null;
                int best_dist;
                for (Polar p : Points) {
                    if (best == null) {
                        best = p;
                        //best_dist = 
                    } else {                        
                    }
                }
                return best;
            }
            
            private ArrayList<Polar> Points = new ArrayList<Polar>();
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

    static {
        //RobotContext.showNavigationBar();

        addObstacles();

        //RobotContext.useObstacle(bar(200, 100, Color.red), 250, 250);
        //RobotContext.useTarget(bar(200, 100, Color.red), 250, 250);
        //RobotContext.useObstacle(bar(300, 20, Color.green), 250, 350);
        //RobotContext.useObstacle(bar(20, 300, Color.blue), 150, 250);
        //RobotContext.useObstacle(bar(20, 300, Color.yellow), 350, 250);
        //RobotContext.useObstacle(circle(20, Color.black), 250, 250);
    }

    /*
        private static GGBitmap circle(int radius, Color color) {
            GGBitmap bm = new GGBitmap(2 * radius, 2 * radius);
            bm.setPaintColor(color);
            bm.setLineWidth(3);
            bm.drawCircle(new Point(radius, radius), radius - 1);
            return bm;
        }
     */

    private static GGBitmap bar(int width, int length, Color color) {
        GGBitmap bm = new GGBitmap(width, length);
        bm.setPaintColor(color);
        bm.fillRectangle(new Point(0, 0), new Point(width - 1, length - 1));
        return bm;
    }

    static class Robot {
        public static final int OneTurnTime = 2190;
        public Gear gear;
        public UltrasonicSensor us;
        public LegoRobot robot;
        
        private int x;
        private int y;
        private double angle;
        
        int getX() { return gear.getX(); }
        int getY() { return gear.getY(); }
        double getAngle() { return angle; }
        
        void gearRotate() { gear.right(); }

        int GetDistance() {
            int d = us.getDistance();
            //return (d==255 ? -1 : d);
            return d;
        }
        
        // Converts angle to [-pi, pi] interval
        private static double NormalizeAngle(double a) {
            while (a > Math.PI)
                a -= 2 * Math.PI;
            while (a < -Math.PI)
                a += 2 * Math.PI;
            return a;
        }
        
        void Rotate(double a) {
            System.out.println("RL:"+a);
            a = NormalizeAngle(a);

            //if (Math.abs(a) < 0.01)
            //    return;

            angle = NormalizeAngle(angle + a);

            final int time = (int) ((double)OneTurnTime * Math.abs(a) / (2 * Math.PI));
            if (a > 0) {
                gear.right(time);
            } else {
                gear.left(time);
            }
            Tools.delay(time+100);
        }
        
        Robot(int xx, int yy) {
            x = xx;
            y = yy;
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
        }
    }
    
    static TangentBug.TangentGraph ScanAround(Robot r)
    {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph();
        
        Tools.startTimer();
        r.gearRotate();
        
        tg.Visit(r.GetDistance(), 0);
        while (Tools.getTime() < Robot.OneTurnTime) {
            int d = r.GetDistance();
            double local_angle = 2 * Math.PI * Tools.getTime() / Robot.OneTurnTime;
            double global_angle = local_angle + r.getAngle();
            tg.Visit(d, global_angle);
            Tools.delay(10);
        }
        r.gear.stop();
        Tools.delay(1000);
        //tg.Visit(r.GetDistance(), 360);
        tg.Finish();
        
        return tg;
    }
    
    static void TillObstacle(Robot r, int dist)
    {
        while (true) {
            final int d = r.us.getDistance();
            if (d >= 0 && d <= dist) {
                return;
            }
            Tools.delay(10);
        }
    }

    static void RunRobot(Robot r) {
        r.gear.forward();
        TillObstacle(r, 100);
        r.gear.stop();
        //Tools.delay(1000);
        TangentBug.TangentGraph tg = ScanAround(r);
        TangentBug.Polar best = tg.GetBestRoute(0,0,0,0,0);
        r.Rotate(best.angle);
        r.gear.forward();
        
        Tools.delay(5000);
        
        r.robot.exit();
    }

    public static void main(String[] args) {
        try {
            Robot r = new Robot(100, 20);
            Tools.delay(1000);
            r.Rotate(Math.PI/3);
            Tools.delay(2000);
            RunRobot(r);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }
}
