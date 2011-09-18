package org.brukhman.ordermap;

import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.Sets;

final class DeleteExecutionsModification extends Modification {

	/**
	 * The order being added.
	 */
	private final Set<UUID> executionIds; 
	private final UUID orderId;
	
	/**
	 * Create a new instance.
	 * 
	 * @param order
	 */
	public DeleteExecutionsModification(UUID orderId, Iterable<UUID> executionIds) {
		checkNotNull(orderId);
		checkNotNull(executionIds);
		this.executionIds = Sets.newHashSet(executionIds);
		this.orderId = orderId;
	}

	public DeleteExecutionsModification(UUID orderId, UUID executionId) {
		this(orderId, Sets.newHashSet(executionId));
	}
	
	@Override
	public void modify(OrderState state) {
		state.deleteExecutions(orderId, executionIds);
	}

}
