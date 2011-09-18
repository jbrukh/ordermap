package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

final class OrderState {

	// main data objects
	Map<UUID, Order> 		orders;
	Map<UUID, Execution> 	executions;
	
	// indices on the data objects that define
	// child relationships
	SetMultimap<UUID, UUID> order2exec;
	
	/**
	 * Create a new, basic instance.
	 */
	OrderState() {
	}
	
	/**
	 * Create an empty order state;
	 * @return
	 */
	public final static OrderState emptryOrderState() {
		OrderState state = new OrderState();
		state.orders = Maps.newHashMap();
		state.executions = Maps.newHashMap();
		state.order2exec = LinkedHashMultimap.create();
		return state;
	}
}
