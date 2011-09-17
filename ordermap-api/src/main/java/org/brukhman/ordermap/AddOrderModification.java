package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

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
	public void modifyAction(Map<UUID, Order> orders,
			Map<UUID, Execution> executions) {
		orders.put(order.getId(), order);
	}

}
