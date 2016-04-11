/*
 * Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.Serializable;

/**
 * 
 * @author Y.D.Zakovryashin, 2014-2015
 * @version 1.0
 */
public enum Command implements Serializable {
	STOP, FORWARD, BACKWARD, LEFT, RIGHT;
	/**
	 * Command code in byte value.
	 */
	private byte code;

	/**
	 * 
	 */
	Command() {
		code = (byte) this.ordinal();
	}

	/**
	 * 
	 * @param value
	 */
	Command(int value) {
		code = (byte) value;
	}

	/**
	 * 
	 * @return
	 */
	public byte code() {
		return code;
	}
}
