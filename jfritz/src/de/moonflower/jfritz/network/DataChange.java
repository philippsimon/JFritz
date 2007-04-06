package de.moonflower.jfritz.network;

import java.io.Serializable;

import java.util.Vector;

/**
 * Class used to store data change operations for external usage
 * 
 * @author brian
 *
 */
public class DataChange<E> implements Serializable {

	public static final long serialVersionUID = 100;
	
	public enum Operation {ADD, REMOVE}
	
	public Operation operation;
	
	public enum Destination {CALLLIST, PHONEBOOK}
	
	public Destination destination;
	
	public String message;
	
	public Vector<E> data;
	
	public DataChange(){
		data = new Vector<E>();
	}
	
	
}
