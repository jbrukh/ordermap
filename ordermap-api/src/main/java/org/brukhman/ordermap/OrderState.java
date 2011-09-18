package org.brukhman.ordermap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

final class OrderState {

	// main data objects
	final Map<UUID, Order> 		orders;
	final Map<UUID, Execution> 	executions;
	
	// indices on the data objects that define
	// child relationships
	final SetMultimap<UUID, UUID> order2exec;
	
	/**
	 * Create a new, basic instance.
	 */
	OrderState(Map<UUID, Order> orders, Map<UUID, Execution> executions) {
		checkNotNull(orders);
		checkNotNull(executions);
		this.orders = orders;
		this.executions = executions;
		
		// TODO: must index here!
		this.order2exec = LinkedHashMultimap.create();
	}
	
	/**
	 * Create a new instance with empty state.
	 */
	public OrderState() {
		this(	
				Maps.<UUID, Order>newHashMap(), 
				Maps.<UUID, Execution>newHashMap()
			);
	}
	
	/**
	 * Put a new execution into the state.
	 *  
	 * @param id
	 * @param order
	 */
	public final void put(UUID id, Execution execution) {
		UUID orderId = execution.getOrderId();
		UUID executionId = execution.getId();
		
		Preconditions.checkState(orders.containsKey(orderId), 
				"No parent order found for this execution.");
		
		executions.put(id, execution);
		order2exec.put(orderId, executionId);
	}
	
	/**
	 * Put a new order into the state.
	 *  
	 * @param id
	 * @param order
	 */
	public final void put(UUID id, Order order) {
		orders.put(id, order);
	}
	
	/**
	 * Resolves the order object.
	 * 
	 * @param id
	 * @return
	 */
	public final Order getOrder(UUID id) {
		return orders.get(id);
	}


	/**
	 * Resolves the execution object.
	 * 
	 * @param id
	 * @return
	 */
	public final Execution getExecution(UUID id) {
		return executions.get(id);
	}
	
	/**
	 * Get the orders.
	 * 
	 * @return
	 */
	public final List<Order> getOrders() {
		return Lists.newArrayList(orders.values());
	}
	
	/**
	 * Get the executions.
	 * 
	 * @return
	 */
	public final List<Execution> getExecutions() {
		return Lists.newArrayList(executions.values());
	}
	
	/**
	 * Delete executions.
	 * 
	 * @param orderId
	 * @param executionIds
	 */
	public final void deleteExecutions(UUID orderId, Set<UUID> executionIds) {
		// resolve these executions and validate
		Map<UUID, Execution> resolvedExecutions = Modificiations.resolve(executionIds, executions);
				
		// collect the modified executions
		for (Execution execution : resolvedExecutions.values()) {
			Execution deletedExecution = new Execution(execution);
			checkState(execution.getOrderId() == orderId, 
							"This execution is not from the specified order: " + execution);
			deletedExecution.isDeleted = true;
			
			// put it in the map, replacing the old versions
			resolvedExecutions.put(execution.getId(), execution);
		}
		executions.putAll(resolvedExecutions);
	}

	/**
	 * Delete an order.
	 * 
	 * @param orderId
	 */
	public final void deleteOrder(UUID orderId) {
		Order original = orders.get(orderId);
		checkNotNull(original);
		
		// create a deleted version
		Order order = new Order(original);
		order.isDeleted = true;
		
		// supersedes the previous version
		orders.put(order.getId(), order);
		
		// find the child executions
		Set<UUID> children = order2exec.get(orderId);
		
		deleteExecutions(orderId, children); // no Exception thrown here, because children guaranteed by logic
	}
}
