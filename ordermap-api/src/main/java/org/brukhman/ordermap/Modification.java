package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.collect.SetMultimap;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.ILock;

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
		Preconditions.checkNotNull(lock);
		this.lockObject = lock;
	}
	
	/**
	 * Obtains a distributed lock on this order
	 * and performs the modification.
	 * @param order2exec TODO
	 */
	final void actOn(Map<UUID, Order> orders, Map<UUID, Execution> executions, 
			SetMultimap<UUID, UUID> order2exec) {
		ILock lock = Hazelcast.getLock(lockObject);
		lock.lock();
		try {
			modify(orders, executions, order2exec);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * User must fill in the action. Any action undertaken
	 * in this method is under distributed lock indexed
	 * by the provided lockObject above. Usually, the lock object
	 * is the order id.
	 * @param order2exec TODO
	 */
	public abstract void modify(Map<UUID, Order> orders, Map<UUID, Execution> executions, 
			SetMultimap<UUID, UUID> order2exec);
	
}
