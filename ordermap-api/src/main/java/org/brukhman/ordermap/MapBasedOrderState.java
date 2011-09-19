package org.brukhman.ordermap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * 
 * @author jbrukh
 *
 */
final class MapBasedOrderState implements OrderState {

	// main data objects
	final Map<UUID, Order> 		orders;
	final Map<UUID, Execution> 	executions;

	// indices on the data objects that define
	// child relationships
	final SetMultimap<UUID, UUID> order2exec;

	final ReadWriteLock lock = new ReentrantReadWriteLock();


	/**
	 * Create a new, basic instance.
	 */
	MapBasedOrderState(Map<UUID, Order> orders, Map<UUID, Execution> executions) {
		checkNotNull(orders);
		checkNotNull(executions);
		this.orders = orders;
		this.executions = executions;

		this.order2exec = LinkedHashMultimap.create();
		for (Execution execution : executions.values()) {
			order2exec.put(execution.getOrderId(), execution.getId());
		}
	}

	/**
	 * Create a new instance with empty state.
	 */
	public MapBasedOrderState() {
		this(	
				Maps.<UUID, Order>newHashMap(), 
				Maps.<UUID, Execution>newHashMap()
				);
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#put(java.util.UUID, org.brukhman.ordermap.Execution)
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#put(java.util.UUID, org.brukhman.ordermap.Execution)
	 */
	@Override
	public final void put(UUID id, Execution execution) {
		lock.writeLock().lock();
		try {
			UUID orderId = execution.getOrderId();
			UUID executionId = execution.getId();

			Preconditions.checkState(orders.containsKey(orderId), 
					"No parent order found for this execution.");

			executions.put(id, execution);
			order2exec.put(orderId, executionId);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#put(java.util.UUID, org.brukhman.ordermap.Order)
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#put(java.util.UUID, org.brukhman.ordermap.Order)
	 */
	@Override
	public final void put(UUID id, Order order) {
		lock.writeLock().lock();
		try {
			orders.put(id, order);
		} finally {
			lock.writeLock().lock();
		}
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#getOrder(java.util.UUID)
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#getOrder(java.util.UUID)
	 */
	@Override
	public final Order getOrder(UUID id) {
		lock.readLock().lock();
		try {
			return orders.get(id);
		} finally {
			lock.readLock().unlock();
		}
	}


	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#getExecution(java.util.UUID)
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#getExecution(java.util.UUID)
	 */
	@Override
	public final Execution getExecution(UUID id) {
		lock.readLock().lock();
		try {
			return executions.get(id);
		} finally {
			lock.readLock().unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#getOrders()
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#getOrders()
	 */
	@Override
	public final List<Order> getOrders() {
		lock.readLock().lock();
		try {
			return Lists.newArrayList(orders.values());
		} finally {
			lock.readLock().unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#getExecutions()
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#getExecutions()
	 */
	@Override
	public final List<Execution> getExecutions() {
		lock.readLock().lock();
		try {
			return Lists.newArrayList(executions.values());
		} finally {
			lock.readLock().unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#deleteExecutions(java.util.UUID, java.util.Set)
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#deleteExecutions(java.util.UUID, java.util.Set)
	 */
	@Override
	public final void deleteExecutions(UUID orderId, Set<UUID> executionIds) {
		lock.writeLock().lock();
		try {
			// resolve these executions and validate
			Map<UUID, Execution> resolvedExecutions = Modificiations.resolve(executionIds, executions);

			// collect the modified executions
			for (Execution execution : resolvedExecutions.values()) {
				Execution deletedExecution = new Execution(execution);
				checkState(execution.getOrderId() == orderId, 
						"This execution is not from the specified order: " + execution);
				deletedExecution.isDeleted = true;

				// put it in the map, replacing the old versions
				resolvedExecutions.put(execution.getId(), execution);
			}
			executions.putAll(resolvedExecutions);
		} finally {
			lock.writeLock().unlock();
		}

	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#deleteExecution(java.util.UUID, java.util.UUID)
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#deleteExecution(java.util.UUID, java.util.UUID)
	 */
	@Override
	public final void deleteExecution(UUID orderId, UUID executionId) {
		deleteExecutions(orderId, Sets.newHashSet(executionId));
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#deleteOrder(java.util.UUID)
	 */
	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.OrderState#deleteOrder(java.util.UUID)
	 */
	@Override
	public final void deleteOrder(UUID orderId) {
		lock.writeLock().lock();
		try {
			Order original = orders.get(orderId);
			checkNotNull(original);

			// create a deleted version
			Order order = new Order(original);
			order.isDeleted = true;

			// supersedes the previous version
			orders.put(order.getId(), order);

			// find the child executions
			Set<UUID> children = order2exec.get(orderId);

			deleteExecutions(orderId, children); // no Exception thrown here, because children guaranteed by logic
		} finally {
			lock.writeLock().unlock();
		}
	}
}
