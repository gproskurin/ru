package robo;

import java.util.Map;
import java.util.TreeMap;

public class WallFollower implements IAlgorithm {
    private final Utils.FollowWallDirection Fwd;

    WallFollower(Utils.FollowWallDirection fwd) { Fwd = fwd; }

    private static final int StepDist = 10;
    private static final double TurnAngle = Math.PI / 4; // 45 deg

    private static class Correction {
        int dist_delta;
        double angle_correction;
        int next_step;
        Correction(int dd, double a, int s) { dist_delta=dd; angle_correction=a; next_step=s; }
    };

    private static final Correction[] Corrections = {
        new Correction(5, Utils.deg2rad(0), 30),
        new Correction(10, Utils.deg2rad(5), 20),
        new Correction(20, Utils.deg2rad(20), 20),
        new Correction(30, Utils.deg2rad(20), 10),
        new Correction(50, Utils.deg2rad(40), 15),
        new Correction(Integer.MAX_VALUE, Utils.deg2rad(70), 10),
    };

    private static final int WallDist = 70;
    private Correction FindCorrection(int dist_delta) {
        for (final Correction c : Corrections) {
            if (c.dist_delta >= dist_delta) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void run(IRobot r, int goal_x, int goal_y) {
        boolean first = true;
        while (true) {
            final int next_step_size = check_wall_distance_and_correct(r, first);
            r.forward(next_step_size);
            //CheckExit();
            first = false;
        }
    }

    private int check_wall_distance_and_correct(IRobot r, boolean first) {
        System.out.println("StepAndCorrect");
        final double turn_angle = Fwd.WallOnTheRight ? -TurnAngle : TurnAngle;
        final double rot_angle = first ? turn_angle + Fwd.angle : turn_angle;
        r.rotate(rot_angle);
        int current_wall_dist = r.get_distance();
        if (current_wall_dist < 0) {
            current_wall_dist = Integer.MAX_VALUE;
        }
        final int delta = Math.abs(WallDist - current_wall_dist);
        System.out.println(" - CurrentDist: " + current_wall_dist + "/" + WallDist + " delta: " + delta);
        final Correction correction = FindCorrection(delta);
        assert correction != null;
        assert correction.dist_delta >= delta;
        final boolean correct_to_left = (Fwd.WallOnTheRight && current_wall_dist < WallDist)
                || (!Fwd.WallOnTheRight && current_wall_dist > WallDist);
        final double delta_angle = correction.angle_correction;
        System.out.println(" - DeltaAngle: "+delta_angle);
        final double correction_angle = correct_to_left ? delta_angle : -delta_angle;
        final double rotation_angle = correction_angle - turn_angle;
        System.out.println(" - correction_angle: "+Utils.rad2deg(correction_angle));
        r.rotate(rotation_angle);
        return correction.next_step;
    }
}
