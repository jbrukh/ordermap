package org.brukhman.ordermap;

/**
 * Base class for modifications.
 * 
 * @author jbrukh
 *
 */
abstract class Modification {
	
	/**
	 * Create a new instance.
	 * 
	 * @param orderId
	 */
	Modification() {
	}
	
	/**
	 * Synchronizes onto the string representation of the
	 * lock object.
	 * 
	 * @param order2exec TODO
	 */
	final void applyTo(OrderState state) {		
			modify(state);
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
