/**
 *  Robot platform, version 1.0
 */
package ru.robot.command;


/**
 * @author
 *
 */
public class DataException extends RobotException {

	/**
	 * 
	 */
	public DataException() {
		super("General data exception");
	}

	/**
	 * 
	 * @param msg
	 */
	public DataException(String msg) {
		super(msg);
	}

	/**
	 * 
	 * @param code
	 * @param msg
	 */
	public DataException(int code, String msg) {
		super(code, msg);
	}

	/**
	 * 
	 * @param code
	 * @param msg
	 * @param option
	 */
	public DataException(int code, String msg, Object option) {
		super(code, msg, option);
	}

	/**
	 * @param cause
	 */
	public DataException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataException(String message, Throwable cause) {
		super(message, cause);
	}
}
