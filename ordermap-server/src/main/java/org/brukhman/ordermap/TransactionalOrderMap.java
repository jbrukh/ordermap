package org.brukhman.ordermap;

import java.util.UUID;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

/**
 * The {@link TransactionalOrderMap} provides the interface
 * to data object modification. It stores orders in a distributed
 * cache, and multiple instances of the TransactionalOrderMap
 * can process requests simultaneously.
 * 
 * @author jbrukh
 *
 */
public final class TransactionalOrderMap {
	
	// tom instance
	private static TransactionalOrderMap tom;
	private final static Object lock = new Object();
	
	// where the data is stored
	private final IMap<UUID, Order> orders;
	private final IMap<UUID, Execution> executions;
	private final MultiMap<UUID, UUID> order2executionIndex;
	
	/**
	 * Get the TOM.
	 * 
	 * @return
	 */
	public final TransactionalOrderMap getInstance() {
		synchronized(lock) {
			if (tom == null) {
				tom = new TransactionalOrderMap();
			}
			return tom;
		}
	}
	
	/**
	 * Create a TOM.
	 * 
	 */
	private TransactionalOrderMap() {
		orders = Hazelcast.getMap("orderMap");
		executions = Hazelcast.getMap("executionMap");
		order2executionIndex = Hazelcast.getMultiMap("order2execution");
	}

	
	/**
	 * Resolve an order.
	 * 
	 * @param id
	 * @return
	 */
	final Order getOrder(UUID id) {
		// this goes straight for the data; since
		// this is the server-side the data should
		// be available locally
		return orders.get(id);
	}
	
	/**
	 * Resolve an execution.
	 * 
	 * @param id
	 * @return
	 */
	final Execution getExecution(UUID id) {
		return executions.get(id);
	}
	
	/**
	 * Add a new order object.
	 * 
	 * @param order
	 */
	final void addOrder(Order order) {
		UUID orderId = order.getId();
		ILock lock = Hazelcast.getLock(orderId);
		lock.lock();
		try {
			// TODO: validation goes here
			
			// goes into the cluster transactionally
			orders.put(orderId, order);
		} finally {
			lock.unlock();
		}
	}
	
	final void addExecution(Execution execution) {
		// assured at execution creation time
		UUID orderId = execution.getOrderId();
		
		ILock lock = Hazelcast.getLock(orderId);
		lock.lock();
		try {
			// TODO: validation goes here
			
			
			executions.put(execution.getId(), execution);
		} finally {
			lock.unlock();
		}
	}
}
