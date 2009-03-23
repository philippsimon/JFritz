package de.moonflower.jfritz.network;

import java.io.IOException;
import java.io.ObjectOutputStream;

import java.net.InetAddress;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

import de.moonflower.jfritz.callerlist.filter.*;
import de.moonflower.jfritz.network.Login;
import de.moonflower.jfritz.struct.Call;
import de.moonflower.jfritz.struct.Person;
import de.moonflower.jfritz.utils.Debug;

/**
 * This class is used for filtering and sending data to a client.
 * It contains a queue of current changes to be sent / filtered
 * and it called by the main ClientConnectionThread running for
 * the connection. It uses a minimal locking to make sure that
 * a slow connection doesnt block any other parts of jfritz.
 * 
 * @see de.moonflower.jfritz.network.ClientConnectionListener
 * 
 * @author brian
 *
 */
public class ServerSenderThread extends Thread {

	private boolean stop;
	
	private DataChange<?> change;
	
	private DataChange<Call> callChange;
	
	private DataChange<Person> contactChange;
	
	private InetAddress remoteAddress;
	
	private ObjectOutputStream objectOut;
	
	private Cipher outCipher;
	
	private ConcurrentLinkedQueue<DataChange> changedObjects;
	
	private Login login;
	
	private Vector<Call> filteredCalls;
	
	private String contactFilter;
	
	private Vector<Person> filteredContacts;
	
	public ServerSenderThread(ObjectOutputStream oos, InetAddress rAddress, Login login, Cipher cipher){
		
		objectOut = oos;
		outCipher = cipher;
		remoteAddress = rAddress;
		changedObjects = new ConcurrentLinkedQueue<DataChange>();
		this.login = login;
		contactFilter = login.contactFilter;
	}
	
	public void run(){
		
		Debug.netMsg("Sender thread for "+remoteAddress+" started up");
		while(!stop){
			
			// if no changes are present then sleep
			if(changedObjects.size() == 0){
				try{
					synchronized(this){
						wait();
					}
				}catch(InterruptedException e){
					Debug.netMsg("A Server sender thread was interrupted!");
		        	Thread.currentThread().interrupt();
				}
			
			} else {
				
				// Iterate over all changes to be sent
				// while is used instead of for because the size of the queue
				// may change during each iteration as the queue is only locked
				// for a short period of time
				while(changedObjects.size() > 0) {
				
					// first take the head of the queue while locking it
					synchronized(this) {
						change = changedObjects.poll();
					}
					
					if(change.destination == DataChange.Destination.CALLLIST){
						//set the reference to DataChange<Call> so we can modify members
						callChange = (DataChange<Call>) change;
						filterCallData((Vector<Call>) change.data);
					}
					
					Debug.netMsg("Writing filtered data to client "+remoteAddress);
					
					// now write it accross the socket connection while leaving the queue open for writing
					try{
							
						SealedObject sealed_object = new SealedObject(change, outCipher);
						objectOut.writeObject(sealed_object);
						objectOut.flush();
						objectOut.reset();
							
					}catch(IOException e){
						Debug.error("Error writing change information to client! host: "+remoteAddress);
						Debug.error(e.toString());
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						Debug.error("Illegal block size exception!");
						Debug.error(e.toString());
						e.printStackTrace();
					}
					
					// implicitly remove the reference to the change object
					change = null;
					
				}
				
			}
		}
		
		Debug.netMsg("Sender Thread for "+remoteAddress+" stopping");
		
	}
	
	/**
	 * Adds the changes to the queue
	 * 
	 * @param changes to sent to the client
	 */
	public synchronized void addChange(DataChange<?>  changes){
		changedObjects.add(changes);
		notify();
	}
	
	/**
	 * Is called to close down the this thread
	 *
	 */
	public synchronized void stopThread(){
		stop = true;
		notify();
	}
	
	
	/**
	 * This function filters the calls
	 * before they are sent to the client
	 * 
	 * @param calls to be filtered
	 */
	public void filterCallData(Vector<Call> calls){
		
		boolean passed = true;
		filteredCalls = new Vector<Call>();
		
		// always get the callFilters reference directly from the login object
		// because this may change through user editing
		Vector<CallFilter> callFilters = login.callFilters;
		
		Debug.netMsg("Filtering outgoing call data for: "+this.remoteAddress
				+" size of calls: "+calls.size());
		for(Call call: calls){
		
			for(CallFilter cf: callFilters){
				if(!cf.passFilter(call)){
					passed = false;
					break;
				}
			}
			if(passed){
				filteredCalls.add(call);
			}
			passed = true;
		}
		
		callChange.data = filteredCalls;
	}
	
}
