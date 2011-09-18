package org.brukhman.ordermap;

import java.util.UUID;

import com.google.common.base.Preconditions;

/**
 * Add an execution. The parent order of this execution
 * must already exist in the state, or an {@link IllegalStateException}
 * is thrown.
 * 
 * @author jbrukh
 */
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

	/**
	 * @throws IllegalStateException if the parent order doesn't exist
	 */
	@Override
	public void modify(OrderState state) {
		UUID orderId = execution.getOrderId();
		UUID executionId = execution.getId();
		
		
		state.put(executionId, execution);
	}

}
