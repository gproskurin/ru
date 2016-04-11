/*
 *  Robot platform, version 1.0
 */
package ru.robot.command;

/**
 * Interface for robot\'s motors.
 * Base error code is 300.
 * @author Y.D.Zakovryashin, 2015
 * @version 1.0
 */
public interface IMotor {
	/**
	 * 
	 * @throws RobotException
	 */
	void start() throws RobotException;

	/**
	 * Starts a motor with current speed, but the motor can execute only limited
	 * promptness (rotations).
	 * 
	 * @param promptness
	 *            limit number of rotations.
	 * @throws RobotException
	 */
	void start(int promptness) throws RobotException;

	/**
	 * 
	 * @throws RobotException
	 */
	void stop() throws RobotException;

	/**
	 * Sets speed of a motor
	 * 
	 * @param speed
	 *            speed of rotation. The value of speed may be positive (forward
	 *            motion), negative (backward motion) or 0 (no motion).
	 * @throws RobotException
	 */
	void setSpeed(int speed) throws RobotException;

	/**
	 * Return current speed
	 * 
	 * @return
	 */
	int getSpeed();

	/**
	 * Returns current value of tachometer
	 * @return
	 */
	int getTachometer();

	void setTachometer(int value) throws RobotException;
}
