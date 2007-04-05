package de.moonflower.jfritz.network;

import java.util.Vector;

/**
 * Class used to store data change operations for external usage
 * 
 * @author brian
 *
 */
public class DataChange<E> {

	public enum Operation {ADD, REMOVE}
	
	public Operation operation;
	
	public enum Destination {CALLLIST, PHONEBOOK}
	
	public Destination destination;
	
	public String message;
	
	public Vector<E> data;
	
}
