package org.brukhman.ordermap;

import static com.google.common.base.Preconditions.*;

final class AddOrderModification implements Modification {

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
		checkNotNull(order);
		this.order = order;
	}

	@Override
	public void modify(OrderState state) {
		state.put(order.getId(), order);
	}
	
	public String toString() {
		return "{Adding " + order + "}";
	}

}
