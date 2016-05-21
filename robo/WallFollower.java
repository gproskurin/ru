package robo;

import java.util.Map;
import java.util.TreeMap;

public class WallFollower implements IAlgorithm {
    private final Utils.FollowWallDirection Fwd;

    WallFollower(Utils.FollowWallDirection fwd) { Fwd = fwd; }

    static private double deg2rad(int deg)
    {
        return Math.PI / 180 * deg;
    }

    private static final int StepDist = 20;
    private static final double TurnAngle = Math.PI / 4; // 45 deg

    private static final int WallDist = 80;
    private static final TreeMap<Integer,Double> WallDeltas = new TreeMap<Integer,Double>();
    static {
        WallDeltas.put(0, 0.0);
        WallDeltas.put(5, 0.0);
        WallDeltas.put(10, deg2rad(10));
        WallDeltas.put(20, deg2rad(20));
        WallDeltas.put(40, deg2rad(45));
        WallDeltas.put(Integer.MAX_VALUE, deg2rad(45));
    }

    @Override
    public void run(IRobot r, int goal_x, int goal_y) {
        boolean first = true;
        while (true) {
            check_wall_distance_and_correct(r, first);
            r.forward(StepDist);
            //CheckExit();
            first = false;
        }
    }

    private void check_wall_distance_and_correct(IRobot r, boolean first) {
        final double turn_angle = Fwd.WallOnTheRight ? TurnAngle : -TurnAngle;
        final double rot_angle = first ? turn_angle + Fwd.angle : turn_angle;
        r.rotate(rot_angle);
        final int current_wall_dist = r.get_distance();
        final int delta = Math.abs(WallDist - current_wall_dist);
        final Map.Entry<Integer,Double> entry = WallDeltas.lowerEntry(delta);
        assert entry != null;
        assert entry.getKey() <= delta;
        final boolean correct_to_left = (Fwd.WallOnTheRight && current_wall_dist < WallDist)
                || (!Fwd.WallOnTheRight && current_wall_dist > WallDist);
        final double correction_angle = correct_to_left ? -entry.getValue() : entry.getValue();
        final double rotation_angle = correction_angle - turn_angle;
    }
}
