/*
 *  Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.IOException;

import lejos.nxt.SensorPort;

/**
 * 
 * Base error code is 400.
 * @author Y.D.Zakovryashin, 2015
 * @version 1.0
 */
public interface ISensor {
	/**
	 * 
	 * @return
	 */
	int getType();

	/**
	 * 
	 * @return
	 */
	int getPort();

	/**
	 * 
	 * @return
	 */
	SensorPort getSensorPort();

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	Data getData() throws IOException;
}
