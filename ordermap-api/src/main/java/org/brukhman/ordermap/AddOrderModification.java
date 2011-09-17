package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.SetMultimap;

final class AddOrderModification extends Modification {

	/**
	 * The order being added.
	 */
	private final Order order; 
	
	/**
	 * Create a new instance.
	 * 
	 * @param order
	 */
	public AddOrderModification(Order order) {
		super(order.getId());
		this.order = order;
	}

	@Override
	public void modify(Map<UUID, Order> orders,
			Map<UUID, Execution> executions, SetMultimap<UUID, UUID> order2exec) {
		orders.put(order.getId(), order);
	}

}
