package org.brukhman.ordermap;

import java.util.Collection;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;

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
	
	private final Multimap<UUID, UUID> order2exec;
	
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
		
		order2exec = HashMultimap.create(); // map orderId => execIds	
		for (Execution execution : executions.values()) {
			order2exec.put(execution.getOrderId(), execution.getId());
		}
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
		new AddOrderModification(order).modify(orders, executions);
	}
	
	final void removeOrder(UUID orderId) {
		ILock lock = Hazelcast.getLock(orderId);
		lock.lock();
		try {
			// TODO: validation goes here
			
			
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Add an execution.
	 * 
	 * @param execution
	 */
	final void addExecution(Execution execution) {
		new AddExecutionModification(execution).modify(orders, executions);
	}
	
	final void removeExecution(UUID executionId) {
		// assured at execution creation time
		Execution execution = getExecution(executionId);
		if(execution == null) {
			return;
		}
		
		ILock lock = Hazelcast.getLock(execution.getOrderId());
		lock.lock();
		try {
			executions.remove(executionId);
			order2exec.remove(execution.getOrderId(), executionId);
		} finally {
			lock.unlock();
		}
	}
}
