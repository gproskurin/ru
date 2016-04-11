/**
 * 
 */
package ru.robot.command;

/**
 * @author Y.D.Zakovryashin, 2015.
 * @version 1.0
 *
 */
public class RobotException extends Exception {
	/**
	 * 
	 */
	public static final long serialVersionUID = 201001001L;
	protected int code;
	protected Object option;

	/**
	 * 
	 */
	public RobotException() {
		this(-1, "General robot exception", null);
	}

	/**
	 * @param message
	 */
	public RobotException(String message) {
		this(-1, message, null);
	}

	/**
	 * 
	 * @param code
	 * @param message
	 */
	public RobotException(int code, String message) {
		this(code, message, null);
	}

	/**
	 * 
	 * @param code
	 * @param message
	 * @param option
	 */
	public RobotException(int code, String message, Object option) {
		super(message);
		this.code = code;
		this.option = option;
	}

	/**
	 * @param cause
	 */
	public RobotException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RobotException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 
	 * @return
	 */
	public Object getOption() {
		return option;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Exception " + code + ": " + option.toString();
	}
}
