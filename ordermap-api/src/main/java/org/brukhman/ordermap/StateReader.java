package org.brukhman.ordermap;

import java.util.List;
import java.util.UUID;

/**
 * Represents a state of the order-execution relationship.
 * 
 * @author jbrukh
 *
 */
interface StateReader {

	/**
	 * Resolves the order object.
	 * 
	 * @param id
	 * @return
	 */
	public abstract Order getOrder(UUID id);

	/**
	 * Resolves the execution object.
	 * 
	 * @param id
	 * @return
	 */
	public abstract Execution getExecution(UUID id);

	/**
	 * Get the orders.
	 * 
	 * @return
	 */
	public abstract List<Order> getOrders();

	/**
	 * Get the executions.
	 * 
	 * @return
	 */
	public abstract List<Execution> getExecutions();

}