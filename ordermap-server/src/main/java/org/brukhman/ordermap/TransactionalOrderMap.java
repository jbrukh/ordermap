package org.brukhman.ordermap;

import java.util.UUID;

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.LinkedHashMultimap;
import com.hazelcast.core.Hazelcast;

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
	private final OrderState state = new OrderState();
	
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
		state.orders = Hazelcast.getMap("orderMap");
		state.executions = Hazelcast.getMap("executionMap");
		
		// TODO: synchronization?
		state.order2exec = LinkedHashMultimap.create(); // map orderId => execIds	
		for (Execution execution : state.executions.values()) {
			state.order2exec.put(execution.getOrderId(), execution.getId());
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
		return state.orders.get(id);
	}
	
	/**
	 * Resolve an execution.
	 * 
	 * @param id
	 * @return
	 */
	public final Execution getExecution(UUID id) {
		return state.executions.get(id);
	}
	
	/**
	 * Add a new order object.
	 * 
	 * @param order
	 */
	final void addOrder(Order order) {
		new AddOrderModification(order)
					.actOn(state);
	}
	
	final void deleteOrder(UUID orderId) {
		new DeleteOrderModification(orderId)
					.actOn(state);
	}
	
	/**
	 * Add an execution.
	 * 
	 * @param execution
	 */
	final void addExecution(Execution execution) {
		new AddExecutionModification(execution)
					.actOn(state);
	}
	
	/**
	 * Delete an executions.
	 * 
	 * @param executionId
	 */
	final void deleteExecution(UUID executionId) {
		Execution execution = state.executions.get(executionId);
		checkNotNull(executionId);
		
		new DeleteExecutionsModification(execution.getOrderId(), executionId)
					.actOn(state);
	}
}
