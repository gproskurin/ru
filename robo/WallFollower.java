package robo;

import java.awt.Point;
import robo.Interfaces.IAlgorithm;
import robo.Interfaces.IRobot;

/*
Алгоритм:
Встаём параллельно стене в направлении обхода.
1. Делаем поворот на 45гр в сторону стены, определяем расстояние до неё.
2. Вычисляем разницу между идеальным расстоянием и измеренным,
по этой разнице определяем величину коррекции угла (поиск в массиве Corrections).
3. Поворачиваемся обратно на 45гр +- коррекция.
4. Делаем шаг вперёд.
5. goto 1.
*/
public class WallFollower implements IAlgorithm {
    private final Utils.FollowWallDirection Fwd;
    private int dFollowed = Integer.MAX_VALUE;
    final int dReach;

    WallFollower(Utils.FollowWallDirection fwd, int dReach) {
        Fwd = fwd;
        this.dReach = dReach;
        System.out.println("FollowWall: wallOnTheRight?"+Fwd.WallOnTheRight+" angle:"+Utils.rad2deg(Fwd.angle)+" dReach:"+this.dReach);
    }

    // угол поворота к стене для определения расстояния до неё
    private static final double TurnAngle = Utils.deg2rad(45);

    // идеальное расстояние до стены, поддерживаем его с помощью корректировок
    private static final int WallDist = 70;

    // одна запись корректировки
    private static class Correction {
        int dist_delta; // разница между идеальным расстоянием и фактическим
        double angle_correction; // угол,на который нужно скорректировать
        int next_step; // длина следующего шага
        Correction(int dd, double a, int s) { dist_delta=dd; angle_correction=a; next_step=s; }
    };

    private static final Correction[] Corrections = {
        new Correction(5, Utils.deg2rad(0), 30),
        new Correction(10, Utils.deg2rad(5), 20),
        new Correction(20, Utils.deg2rad(20), 20),
        new Correction(30, Utils.deg2rad(30), 10),
        new Correction(50, Utils.deg2rad(50), 15),
        new Correction(Integer.MAX_VALUE, Utils.deg2rad(70), 10),
    };

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
            final int next_step_size = check_wall_distance_and_correct(r, first, goal_x, goal_y);
            System.out.println("dFollowed:"+dFollowed+" dReach:"+dReach);
            if (dFollowed < dReach) {
                System.out.println("WallFollow END");
                return;
            }
            r.forward(next_step_size);
            first = false;
        }
    }

    // поворот к стене, определение расстояние до неё,
    // вычислении коррекции угла, поворот обратно с учётом коррекции
    private int check_wall_distance_and_correct(IRobot r, boolean first, int goal_x, int goal_y) {
        System.out.println("StepAndCorrect");

        // поворачиваемся на месте к стене, чтобы определить расстояние до неё
        final double turn_angle = Fwd.WallOnTheRight ? -TurnAngle : TurnAngle;
        // на первой итерации мы стоим не параллельно стене,
        // прибавляем угол между направлением робота и направлением стены
        final double rot_angle = first ? turn_angle + Fwd.angle : turn_angle;
        r.rotate(rot_angle);

        // определяем расстояние до стены
        int current_wall_dist = r.get_distance();
        if (current_wall_dist < 0) {
            current_wall_dist = Integer.MAX_VALUE; // для поиска по массиву Corrections
        } else {
            final Point wall = Utils.DecartFromPoint(
                    r.get_x(),
                    r.get_y(),
                    new Utils.Polar(current_wall_dist, r.get_angle())
            );
            final int wall_to_goal = Utils.GetDist(goal_x, goal_y, wall);
            if (wall_to_goal < dFollowed) {
                dFollowed = wall_to_goal;
            }
        }

        // разница между идеальным расстоянием и измеренным
        final int delta = Math.abs(WallDist - current_wall_dist);
        System.out.println(" - CurrentDist: " + current_wall_dist + "/" + WallDist + " delta: " + delta);

        // ищем поправки
        final Correction correction = FindCorrection(delta);
        assert correction != null;
        assert correction.dist_delta >= delta;

        // если стена справа и расстояние до неё меньше идеального, то мы должны
        // отвернуть от стены, т.е. повернуть влево
        // и т.п.
        final boolean correct_to_left = (Fwd.WallOnTheRight && current_wall_dist < WallDist)
                || (!Fwd.WallOnTheRight && current_wall_dist > WallDist);
        System.out.println(" - DeltaAngle: "+Utils.rad2deg(correction.angle_correction));
        final double correction_angle = correct_to_left
                ? correction.angle_correction
                : -correction.angle_correction;

        // угол поворота, с учётом поворота обратно параллельно стене и коррекции
        final double rotation_angle = correction_angle - turn_angle;
        System.out.println(" - correction_angle: "+Utils.rad2deg(correction_angle));

        // поворачиваемся
        r.rotate(rotation_angle);

        // возвращаем величину следующего шага
        return correction.next_step;
    }
}
