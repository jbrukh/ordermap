package org.brukhman.ordermap;

import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.hazelcast.core.Hazelcast;
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
		
		// TODO: synchronization?
		order2exec = LinkedHashMultimap.create(); // map orderId => execIds	
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
	public final Order getOrder(UUID id) {
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
	public final Execution getExecution(UUID id) {
		return executions.get(id);
	}
	
	/**
	 * Add a new order object.
	 * 
	 * @param order
	 */
	final void addOrder(Order order) {
		new AddOrderModification(order)
					.actOn(orders, executions, null);
	}
	
	final void deleteOrder(UUID orderId) {
		new DeleteOrderModification(orderId)
					.actOn(orders, executions, null);
	}
	
	/**
	 * Add an execution.
	 * 
	 * @param execution
	 */
	final void addExecution(Execution execution) {
		new AddExecutionModification(execution)
					.actOn(orders, executions, null);
	}
	
	/**
	 * Delete an executions.
	 * 
	 * @param executionId
	 */
	final void deleteExecution(UUID executionId) {
		Execution execution = executions.get(executionId);
		Preconditions.checkNotNull(executionId);
		
		new DeleteExecutionsModification(execution.getOrderId(), Sets.newHashSet(executionId))
					.actOn(orders, executions, null);
	}
}
