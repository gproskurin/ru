/**
 *  Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.Serializable;

/**
 * The enumeration presents event types.
 * 
 * @author Y.D.Zakovryashin, 2015.
 * @version 1.0
 */
public enum EventType implements Serializable {
	COMMAND, EVENT, DATA, MESSAGE;
}
