/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
                    System.out.println("Dist:"+p.distance+" angle:"+p.angle);
                }
            }
            
            Polar GetBest(/*Polar goal*/) {
                //if (Points.isEmpty())
                //    return goal;
                // TODO
                return Points.get(0);
                //return goal;
            }
            
            private ArrayList<Polar> Points = new ArrayList<Polar>();
        }
    }

    private static void addObstacles() {
        Point mesh[] = {
            new Point(-100, -50),
            new Point(-100, 50),
            new Point(100, 50),
            new Point(100, -50)
        };
        RobotContext.useTarget(bar(200, 100, Color.red), mesh, 250, 250);
    }

    static {
        RobotContext.setStartPosition(100, 10);
        RobotContext.setStartDirection(60);
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
        public static final int OneTurnTime = 2180;
        public Gear gear;
        public UltrasonicSensor us;
        public LegoRobot robot;
        
        int GetDistance() {
            int d = us.getDistance();
            //return (d==255 ? -1 : d);
            return d;
        }

        Robot() {
            robot = new LegoRobot();

            gear = new Gear();
            robot.addPart(gear);

            //ts = new TouchSensor(SensorPort.S3);
            //robot.addPart(ts);
            us = new UltrasonicSensor(SensorPort.S1);
            us.setBeamAreaColor(Color.green);
            us.setMeshTriangleColor(Color.blue);
            us.setProximityCircleColor(Color.lightGray);
            //us.addUltrasonicListener(listener);
            robot.addPart(us);
        }
    }
    
    static TangentBug.TangentGraph ScanAround(Robot r)
    {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph();
        
        Tools.startTimer();
        r.gear.left();
        
        tg.Visit(r.GetDistance(), 0);
        while (Tools.getTime() < Robot.OneTurnTime) {
            int d = r.GetDistance();
            double angle = 360 * Tools.getTime() / Robot.OneTurnTime;
            tg.Visit(d, angle);
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
        //r.gear.setSpeed(10);
        TillObstacle(r, 100);
        r.gear.stop();
        Tools.delay(1000);
        TangentBug.TangentGraph tg = ScanAround(r);
        TangentBug.Polar best = tg.GetBest();
        final double rotateTime = (double)Robot.OneTurnTime * best.angle / 360;
        r.gear.left();
        Tools.delay((int)rotateTime);
        r.gear.stop();
        r.gear.forward();
        Tools.delay(10000);
        
        r.robot.exit();
        return;
        /*
        r.gear.setSpeed(30);
        r.gear.forward();
        while (true) {
            final double arc = 0.3;
            final int dist = r.us.getDistance();
            System.out.println("Dist: " + dist);
            if (dist > 0 && dist < 100) {
                r.gear.leftArc(arc);
                final int dist1 = r.us.getDistance();
                while (dist1 > 0 && dist1 < 200) {
                    Tools.delay(10);
                }
                r.gear.forward();
                Tools.delay(10000);
            }
            Tools.delay(100);
        }
        //Tools.delay(20000);

        //r.robot.exit();
        */
    }

    public static void main(String[] args) {
        try {
            Robot r = new Robot();
            RunRobot(r);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }
}
