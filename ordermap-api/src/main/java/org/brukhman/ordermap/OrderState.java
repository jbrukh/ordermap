package org.brukhman.ordermap;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the state of data objects. All operations upon them
 * and synchronization of interactions is encapsulated within such
 * a state. All data id resolution happens from this state.
 * 
 * @author ybrukhma
 *
 */
interface OrderState {

	/**
	 * Put a new execution into the state.
	 * 
	 * @param id
	 * @param execution
	 */
	public abstract void put(UUID id, Execution execution);

	/**
	 * Put a new order into the state.
	 * 
	 * @param id
	 * @param order
	 */
	public abstract void put(UUID id, Order order);

	/**
	 * Resolve an order.
	 * 
	 * @param id
	 * @return
	 */
	public abstract Order getOrder(UUID id);

	/**
	 * Resolve an execution.
	 * 
	 * @param id
	 * @return
	 */
	public abstract Execution getExecution(UUID id);

	/**
	 * Get the master list of orders.
	 * 
	 * @return
	 */
	public abstract List<Order> getOrders();

	/**
	 * Get the master list of executions.
	 * 
	 * @return
	 */
	public abstract List<Execution> getExecutions();

	/**
	 * Delete executions.
	 * 
	 * @param orderId
	 * @param executionIds
	 */
	public abstract void deleteExecutions(UUID orderId, Set<UUID> executionIds);

	/**
	 * Delete a single execution (convenience method).
	 * 
	 * @param orderId
	 * @param executionId
	 */
	public abstract void deleteExecution(UUID orderId, UUID executionId);

	/**
	 * Delete an order.
	 * 
	 * @param orderId
	 */
	public abstract void deleteOrder(UUID orderId);

}