package org.brukhman.ordermap;

import java.io.Serializable;

/**
 * A modification of an {@link MapBasedOrderState}.
 *  
 * @author jbrukh
 *
 */
interface Modification extends Serializable {
	
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
