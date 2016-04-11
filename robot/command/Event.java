/**
 *  Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.Serializable;

/**
 * The class presents any command, event, data or message.
 *
 * @author Y.D.Zakovryashin, 2015.
 * @version 1.0.
 */
public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 101100010444L;
	/**
	 * Event type
	 */
	public final EventType type;
	/**
	 * Event identification
	 */
	public int id;
	/**
	 * Event source
	 */
	public Address source;
	/**
	 * Event destination
	 */
	public Address destination;
	/**
	 * Event option
	 */
	public Data data;

	/**
	 * 
	 * @param type
	 * @param source
	 * @param desination
	 * @param data
	 */
	public Event(EventType type, Address source, Address desination, Data data) {
		this.type = type;
		this.id = Math.abs((int) System.currentTimeMillis());
		this.source = source;
		this.destination = desination;
		this.data = data;
	}

	/**
	 * 
	 * @param a
	 */
	public Event(byte[] a) {
		type = EventType.values()[a[0]];
		id = a[1] << 24;
		id |= (a[2] << 24) >>> 8;
		id |= (a[3] << 24) >>> 16;
		id |= (a[4] << 24) >>> 24;
		if (a.length > 5) {
			byte[] tmp = new byte[a.length - 5];
			System.arraycopy(a, 5, tmp, 0, tmp.length);
			data = new Data(tmp);
		}
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getBytes() {
		byte[] t = new byte[(data != null ? data.getDataLength() : 0) + 5];
		t[0] = (byte) type.ordinal();
		t[1] = (byte) (id >> 24);
		t[2] = (byte) (id >> 16);
		t[3] = (byte) (id >> 8);
		t[4] = (byte) id;
		if (data != null) {
			System.arraycopy(data.getData(), 0, t, 5, (t.length - 5));
		}
		return t;
	}

	/**
	 * 
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Event: ").append(type.name()).append(" id=").append(id)
				.append(", source=").append(source).append(", destination=")
				.append(destination);
		if (data != null && data.getDataLength() > 0) {
			sb.append("\n").append(data);
			/*
			 * for (byte b : data) { sb.append(b).append(", "); }
			 */
			sb.append("\n");
		}
		return new String(sb);
	}
}
