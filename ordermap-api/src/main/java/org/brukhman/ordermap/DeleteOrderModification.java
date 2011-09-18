package org.brukhman.ordermap;

import java.util.UUID;

import static com.google.common.base.Preconditions.*;

/**
 * 
 * @author jbrukh
 *
 */
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
		checkNotNull(orderId);
		this.orderId = orderId; 
	}

	@Override
	public void modify(OrderState state) {
		state.deleteOrder(orderId);
	}

}
