package org.brukhman.ordermap;

import static com.google.common.base.Preconditions.*;

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
		checkNotNull(execution);
		this.execution = execution;
	}

	/**
	 * @throws IllegalStateException if the parent order doesn't exist
	 */
	@Override
	public void modify(OrderState state) {
		state.put(execution.getId(), execution);
	}

}
