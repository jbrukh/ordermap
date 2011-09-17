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
	public void modify(OrderState state) {
		state.orders.put(order.getId(), order);
	}

}
