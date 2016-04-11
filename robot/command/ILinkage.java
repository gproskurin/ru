/**
 *  Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.IOException;

/**
 * Base error code is 200.
 * @author Y.D.Zakovryashin, 2015
 * @version 1.0
 */
public interface ILinkage extends AutoCloseable {
	/**
	 * 
	 * @return
	 */
	Data getInfo();

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	Event getEvent() throws IOException;

	/**
	 * 
	 * @param event
	 * @throws IOException
	 */

	void sendEvent(Event event) throws IOException;

	/**
	 * 
	 * @param data
	 * @throws IOException
	 */
	void send(byte[] data) throws IOException;

	/**
	 * 
	 * @param data
	 * @throws IOException
	 */
	void receive(byte[] data) throws IOException;

	/**
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;
}
