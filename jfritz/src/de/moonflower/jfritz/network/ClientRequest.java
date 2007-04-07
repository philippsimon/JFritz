package de.moonflower.jfritz.network;

import java.io.Serializable;

import java.util.Date;
import java.util.Vector;

public class ClientRequest<E> implements Serializable {

	public enum Operation{ ADD, GET, REMOVE}
	
	public Operation operation;
	
	public enum Destination{CALLLIST, PHONEBOOK}
	
	public Destination destination;
	
	public Vector<E> data;
	
	public Date timestamp;
	
}
