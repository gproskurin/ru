/**
 * Robot platform, version 1.0
 */
package ru.robot.command;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is converter for robot data. It provides deserialization service.
 * Base error code 20.
 * 
 * @author Y.D.Zakovryashin, 2014-2015.
 * @version 1.0
 */
public class DataConverter {
	private DataConverter() {
	}

	public static Object getObject(Data data) throws DataException, IOException {
		if (data == null) {
			throw new DataException(21, "Null data");
		}
		int dlen = data.getDataLength();
		int n;
		ByteArrayInputStream bin = new ByteArrayInputStream(data.getData(),
				Data.HEAD_SIZE, (dlen - Data.HEAD_SIZE));
		DataInputStream din = new DataInputStream(bin);
		switch (data.getDataClass()) {
		case BYTE:
			return new Byte(din.readByte());
		case BYTE_ARRAY:
			byte[] b = new byte[dlen - Data.HEAD_SIZE];
			n = bin.read(b);
			if (n != b.length) {
				throw new DataException(22, "Invalid data size");
			}
			return b;
		case BOOLEAN:
			return din.readBoolean();
		case BOOLEAN_ARRAY:
			boolean[] ba = new boolean[dlen - Data.HEAD_SIZE];
			for (int i = 0; i < ba.length; ++i) {
				ba[i] = (bin.read() != 0) ? true : false;
			}
			return ba;
		case INT:
			return din.readInt();
		case INT_ARRAY:
			int[] ia = new int[(dlen - Data.HEAD_SIZE) / Integer.SIZE];
			for (int i = 0; i < ia.length; ++i) {
				ia[i] = din.readInt();
			}
			return ia;
		case LONG:
			return din.readLong();
		case LONG_ARRAY:
			long[] la = new long[(dlen - Data.HEAD_SIZE) / Long.SIZE];
			for (int i = 0; i < la.length; ++i) {
				la[i] = din.readLong();
			}
			return la;
		case FLOAT:
			return din.readFloat();
		case FLOAT_ARRAY:
			float[] fa = new float[(dlen - Data.HEAD_SIZE) / Float.SIZE];
			for (int i = 0; i < fa.length; ++i) {
				fa[i] = din.readFloat();
			}
			return fa;
		case DOUBLE:
			return din.readDouble();
		case DOUBLE_ARRAY:
			double[] da = new double[(dlen - Data.HEAD_SIZE) / Double.SIZE];
			for (int i = 0; i < da.length; ++i) {
				da[i] = din.readDouble();
			}
			return da;
		case CHAR:
			return din.readChar();
		case CHAR_ARRAY:
			char[] ca = new char[(dlen - Data.HEAD_SIZE) / Character.SIZE];
			for (int i = 0; i < ca.length; ++i) {
				ca[i] = din.readChar();
			}
			return ca;
		case STRING:
			return din.readUTF();
		case STRING_ARRAY:
			ArrayList<String> al = new ArrayList<>();
			String tmp;
			while (true) {
				tmp = din.readUTF();
				if (tmp == null) {
					break;
				}
				al.add(tmp);
			}
			return al.toArray(new String[] { null });
		case OBJECT:
			// Unsupported operation yet
			return "Object";
		case OBJECT_ARRAY:
			// Unsupported operation yet
			return "Object[]";
		default:// Invalid data type
			throw new DataException(20, "Undefined data type");
		}
	}
}
