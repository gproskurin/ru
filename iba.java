import lejos.robotics.navigation.RotateMoveController;

// coordinate on map
class Point {
	public int x;
	public int y;
};

type Angle = double;

class RobotPosition {
	Point;
	Angle; // current robot's direction
};

final Direction direction_epsilon = 0.01; // TODO

Direction calc_direction(RobotPosition current, Point goal)
{
	// TODO
}

boolean direction_is_free(RobotPosition current, Direction dir)
{
	// TODO
}

void move_step_to(RobotPosition current, double target_angle)
{
	final double rotate_angle = target_angle - current.angle;
	if (Math.abs(rotate_angle) > epsilon_angle) {
		RotateMoveController.rotate(rotate_angle);
	}
	// Move step
	// TODO
}

public interface IStrategy {
	RobotPosition run(RobotPosition current, Point goal);
}

// Main algorithm
IStrategy::run()
{
	follow_obstacle_boundary(current, goal);
}

RobotPosition follow_obstacle_boundary(RobotPosition current, Goal goal)
{
	while (true) {
		goal_angle = calc_direction(current, goal);
		if (direction_is_free(current, goal_angle)) {
			return current;
		}
		// Move step aroung obstacle
		// TODO
	}
	return current;
}
