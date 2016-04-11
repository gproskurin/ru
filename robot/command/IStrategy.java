/*
 * Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.IOException;

/**
 * Base error code is 1000.
 * @author Y.D.Zakovryashin, 2014-2015.
 * @version 1.0
 *
 */
public interface IStrategy {
	/**
	 * 
	 * @return
	 * @throws RobotException
	 */
	void run() throws RobotException;

	/**
	 * @param code
	 * @throws RobotException
	 */
	void exit(int code) throws RobotException;

	/**
	 * Load data, for example strategy parameters or map.
	 * @param data
	 * @return
	 * @throws IOException
	 */
	boolean loadData(Data data) throws IOException;

	/**
	 * 
	 * @param data
	 * @return
	 * @throws CommandException
	 */
	boolean addSensor(ISensor sensor) throws RobotException;

	/**
	 * 
	 * @param data
	 * @return
	 * @throws CommandException
	 */
	ISensor removeSensor(ISensor sensor) throws RobotException;

	/**
	 * 
	 * @param motor
	 * @return
	 * @throws RobotException
	 */
	boolean addMotor(IMotor motor) throws RobotException;

	/**
	 * 
	 * @param motor
	 * @return
	 * @throws RobotException
	 */
	IMotor removeMotor(IMotor motor) throws RobotException;
}
