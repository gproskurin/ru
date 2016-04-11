/*
 *  Robot platform, version 1.0
 */
package ru.robot.command;

/**
 * 
 * Base error code is 100.
 * @author Y.D.Zakovryashin, 2015
 * @version 1.0
 */
public interface IControl {

	/**
	 * Performs event.
	 * @param event
	 * @return
	 * @throws RobotException
	 */
	boolean perform(Event event) throws RobotException;
}