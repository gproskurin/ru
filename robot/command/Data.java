/**
 *  Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.Serializable;

/**
 * Main class for any data presentation in a serialized form (byte array). Each
 * object of this type is constant object (not mutable). The data has a fix size
 * head and a serialized data. The head has following structure:
 * <ol>
 * <li>first byte always contains general data destination in accordance with
 * {@link DataType DataType} enumeration,</li>
 * <li>second byte always defines Java type of the data.</li>
 * </ol>
 * 
 * @author Y.D.Zakovryashin, 2014-2015.
 * @version 1.1
 */
public class Data implements Serializable {
	public static final int HEAD_SIZE = 2;

	/**
	 * Valid Java class for data
	 */

	/**
	 * Serialization code XXXX (project code) XXXX (version) XXXX (object code)
	 */
	private static final long serialVersionUID = 101100010511L;
	/**
	 * Data type
	 */
	private DataType type;
	/**
	 * Data class
	 */
	private Type dataClass;
	private byte[] data;

	/**
	 * Construct new object with specified parameters.
	 * 
	 * @param type
	 *            general data purpose in accordance with {@link DataType
	 *            DataType}.
	 * @param dataClass
	 *            valid Java type in accordance with {@link Type Type}.
	 * @param data
	 *            byte array of serialized data.
	 */
	public Data(DataType type, Type dataClass, byte[] data) {
		this.data = new byte[HEAD_SIZE + (data != null ? data.length : 0)];
		this.type = type;
		this.data[0] = (byte) type.ordinal();
		this.dataClass = dataClass;
		this.data[1] = dataClass.getCode();
		if (data != null) {
			System.arraycopy(this.data, HEAD_SIZE, data, 0, data.length);
		}
	}

	/**
	 * Constructs object from byte array.
	 * 
	 * @param data
	 *            serialized data.
	 */
	public Data(byte[] data) {
		type = DataType.values()[data[0]];
		dataClass = Type.values()[data[1]];
		this.data = data;
	}

	/**
	 * Returns data class.
	 * 
	 * @return true if data is array.
	 */
	public boolean isArray() {
		return dataClass.getCode() > 0 && dataClass.getCode() % 2 > 0;
	}

	/**
	 * Returns data type in accordance with {@link DataType DataType}.
	 * 
	 * @return data type
	 */
	public DataType getDataType() {
		return type;
	}

	/**
	 * Returns data class.
	 * 
	 * @return data class.
	 */
	public Type getDataClass() {
		return dataClass;
	}

	/**
	 * Returns {@link java.lang.String String} with qualified data type name.
	 * 
	 * @return qualified data type name or null if the object has invalid head.
	 */
	public String getDataClassName() {
		switch (dataClass) {
		case BYTE:
			return "byte";
		case BYTE_ARRAY:
			return "byte[]";
		case BOOLEAN:
			return "boolean";
		case BOOLEAN_ARRAY:
			return "boolean[]";
		case INT:
			return "int";
		case INT_ARRAY:
			return "int[]";
		case LONG:
			return "long";
		case LONG_ARRAY:
			return "long[]";
		case FLOAT:
			return "float";
		case FLOAT_ARRAY:
			return "float[]";
		case DOUBLE:
			return "double";
		case DOUBLE_ARRAY:
			return "double[]";
		case CHAR:
			return "char";
		case CHAR_ARRAY:
			return "char[]";
		case STRING:
			return "String";
		case STRING_ARRAY:
			return "String[]";
		case OBJECT:
			return "Object";
		case OBJECT_ARRAY:
			return "Object[]";
		default:// Invalid data type
			return "Undefined data type";
		}
	}

	/**
	 * Return data length.
	 * 
	 * @return data length.
	 */
	int getDataLength() {
		return data.length;
	}

	/**
	 * Returns all data in byte array form.
	 * 
	 * @return data
	 */
	public byte[] getData() {
		return data;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Data type: ").append(type.name()).append("[");
		sb.append(getDataClassName()).append("]\n");
		return new String(sb);
	}
}
