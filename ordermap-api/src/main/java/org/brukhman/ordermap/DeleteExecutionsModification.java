package org.brukhman.ordermap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.SetMultimap;
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
		super(orderId);
		checkNotNull(executionIds);
		this.executionIds = Sets.newHashSet(executionIds);
		this.orderId = orderId;
	}

	public DeleteExecutionsModification(UUID orderId, UUID executionId) {
		this(orderId, Sets.newHashSet(executionId));
	}
	
	@Override
	public void modify(OrderState state) {
		
		// resolve these executions and validate
		Map<UUID, Execution> resolvedExecutions = Modificiations.resolve(executionIds, state.executions);
		
		// collect the modified executions
		for (Execution execution : resolvedExecutions.values()) {
			Execution deletedExecution = new Execution(execution);
			checkState(execution.getOrderId() == orderId, 
					"This execution is not from the specified order: " + execution);
			deletedExecution.isDeleted = true;
	
			// put it in the map, replacing the old versions
			resolvedExecutions.put(execution.getId(), execution);
		}
		
		// no modification actually goes in until the validation passes
		state.executions.putAll(resolvedExecutions);
	}

}
