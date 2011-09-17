package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;
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
	private final Object lockObject;
	
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
	 */
	final void modify(Map<UUID, Order> orders, Map<UUID, Execution> executions) {
		ILock lock = Hazelcast.getLock(lockObject);
		lock.lock();
		try {
			modifyAction(orders, executions);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * User must fill in the action. Any action undertaken
	 * in this method is under districuted lock indexed
	 * by the provided lockObject above.
	 */
	public abstract void modifyAction(Map<UUID, Order> orders, Map<UUID, Execution> executions);
	
}
