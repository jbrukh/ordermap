package org.brukhman.ordermap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.collect.SetMultimap;

final class DeleteOrderModification extends Modification {

	/**
	 * The order being added.
	 */
	private final UUID orderId;
	
	/**
	 * Create a new instance.
	 * 
	 * @param order
	 */
	public DeleteOrderModification(UUID orderId) {
		super(orderId);
		this.orderId = orderId; 
	}

	@Override
	public void modify(OrderState state) {
		Order original = state.orders.get(orderId);
		Preconditions.checkNotNull(original);
		
		// create a deleted version
		Order order = new Order(original);
		order.isDeleted = true;
		
		// supersedes the previous version
		state.orders.put(order.getId(), order);
		
		// find the child executions
		Set<UUID> children = state.order2exec.get(orderId);
		
		new DeleteExecutionsModification(orderId, children)
				.actOn(state);
	}

}
