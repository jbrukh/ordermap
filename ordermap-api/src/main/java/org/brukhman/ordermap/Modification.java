package org.brukhman.ordermap;

import static com.google.common.base.Preconditions.*;

/**
 * Base class for modifications.
 * 
 * @author jbrukh
 *
 */
abstract class Modification {
	
	/**
	 * Modifications must specify an object to lock
	 * upon; since this will be a distributed lock,
	 * it should be something that serializes to something
	 * standard like a String, UUID, etc.
	 */
	private Object lockObject;
	
	/**
	 * Create a new instance.
	 * 
	 * @param orderId
	 */
	Modification(Object lock) {
		checkNotNull(lock);
		this.lockObject = lock;
	}
	
	/**
	 * Synchronizes onto the string representation of the
	 * lock object.
	 * 
	 * @param order2exec TODO
	 */
	final void actOn(OrderState state) {
		synchronized(lockObject.toString().intern()) {
			modify(state);
		}	
	}
	
	/**
	 * User must fill in the action. Any action undertaken
	 * in this method is under distributed lock indexed
	 * by the provided lockObject above. Usually, the lock object
	 * is the order id.
	 * @param order2exec TODO
	 */
	public abstract void modify(OrderState state);
	
	/**
	 * A listener for {@link Modification}s.
	 * 
	 * @author jbrukh
	 *
	 */
	public static interface Listener {
		/**
		 * A modification occurred.
		 *  
		 * @param modification
		 */
		void modificationOccurred(Modification modification);
	}
}
