package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.SetMultimap;

final class AddExecutionModification extends Modification {

	/**
	 * The order being added.
	 */
	private final Execution execution; 
	
	/**
	 * Create a new instance.
	 * 
	 * @param order
	 */
	public AddExecutionModification(Execution execution) {
		super(execution.getOrderId());
		this.execution = execution;
	}

	@Override
	public void modify(Map<UUID, Order> orders,
			Map<UUID, Execution> executions, SetMultimap<UUID, UUID> order2exec) {
		executions.put(execution.getId(), execution);
	}

}
