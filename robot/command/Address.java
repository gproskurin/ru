/**
 * Robot platform, version 1.0.
 */
package ru.robot.command;

import java.io.Serializable;

/**
 * Класс представляет собой сетевой адрес
 * робота или внешнего устройства в той сети,
 * которая используется для связи между
 * компонентами поисковой системы
 * 
 * @author Y.D.Zakovryashin, 2015
 * @version 1.0
 */
public class Address implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 101100010458L;
	public static final int OFFSET = 4;
	/**
	 * Атрибут представляет порт робота или
	 * устройства
	 */
	public final int port;
	/**
	 * Тип сетевого адреса, который определён
	 * в перечислении {@link ru.robot.command.AddressType
	 * AddressType}, по умолчанию принимается
	 * значение {@link ru.robot.command.AddressType#BLUETOOTH BLUETOOTH}
	 * - адрес в сети Bluetooth
	 */
	public final AddressType type;

	/**
	 * Значение данного атрибута равное true
	 * указывает на доступность адреса для
	 * получения и отправки команд и
	 * сообщений, а значение false - на
	 * недоступность данного адреса.
	 */
	public boolean activity;
	/**
	 * Атрибут содержит значение адреса
	 */
	public final String address;

	/**
	 * Конструктор по умполчанию, который
	 * определяет неопределённый адрес и порт,
	 * а также устанавливает тип адреса по
	 * умолчанию {@link ru.robot.command.AddressType#BLUETOOTH
	 * Bluetooth}.
	 */
	public Address() {
		this(AddressType.BLUETOOTH, -1, null);
	}

	/**
	 * Конструктор, который определяет тип
	 * адреса, собственно адрес и порт.
	 * 
	 * @param type
	 *            - тип адреса, который принимает
	 *            одно из значений, определённых
	 *            {@link ru.robot.command.AddressType AddressType}
	 * @param port
	 *            - порт адреса
	 * @param address
	 *            - значение адреса
	 */
	public Address(AddressType type, int port, String address) {
		this.type = type;
		this.port = port;
		this.address = address;
		this.activity = false;
	}

	/**
	 * 
	 * @param array
	 */
	public Address(byte[] array) {
		type = AddressType.values()[array[0]];
		port = array[1];
		activity = array[2] != 0 ? true : false;
		address = (array.length > 4) ? new String(array, OFFSET, array.length - OFFSET)
				: null;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getBytes() {
		byte[] tmp = new byte[4 + (address != null ? address.length() : 0)];
		tmp[0] = (byte) type.ordinal();
		tmp[1] = (byte) port;
		tmp[2] = activity ? (byte) 1 : (byte) 0;
		if (address != null) {
			System.arraycopy(address.getBytes(), 0, tmp, OFFSET, tmp.length - OFFSET);
		}
		return tmp;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Address [port=" + port + ", type=" + type.name() + ", address="
				+ address + ", active=" + activity + "]";
	}
}
