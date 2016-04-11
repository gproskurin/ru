/**
 *  Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.Serializable;

/**
 * @author Y.D.Zakovryashin, 2014-2015.
 * @version 1.0
 */
public enum DataType implements Serializable {
	RAW, SOUND, VIDEO, POSITION, MAP, RESULT, OBJECT;

	public byte byteValue() {
		return (byte) ordinal();
	}
}
