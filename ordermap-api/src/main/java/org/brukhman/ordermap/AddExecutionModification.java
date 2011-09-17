package org.brukhman.ordermap;


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
	public void modify(OrderState state) {
		state.executions.put(execution.getId(), execution);
	}

}
