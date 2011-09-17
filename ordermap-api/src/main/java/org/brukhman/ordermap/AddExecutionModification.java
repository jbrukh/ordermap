package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

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
	public void modifyAction(Map<UUID, Order> orders,
			Map<UUID, Execution> executions) {
		executions.put(execution.getId(), execution);
	}

}