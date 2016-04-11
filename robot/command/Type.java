/*
 * Robot platform, version 1.0
 */
package ru.robot.command;

/**
 * Valid data types for class {@link Data Data}
 * @author @author Y.D.Zakovryashin, 2015
 * @version 1.0
 */
public enum Type {

	BYTE(0),
	BYTE_ARRAY(1),
	BOOLEAN(2),
	BOOLEAN_ARRAY(3),
	INT(4),
	INT_ARRAY(5),
	LONG(8), 
	LONG_ARRAY(9), 
	FLOAT(16),
	FLOAT_ARRAY(17),
	DOUBLE(32),
	DOUBLE_ARRAY(33),
	CHAR(64),
	CHAR_ARRAY(65),
	STRING(124), 
	STRING_ARRAY(125),
	OBJECT(	126),
	OBJECT_ARRAY(127), 
	UNKNOWN(-1);
	private byte code;

	/**
	 * 
	 * @param code
	 */
	Type(int code) {
		this.code = (byte) code;
	}

	/**
	 * 
	 * @return
	 */
	byte getCode() {
		return code;
	}
}
