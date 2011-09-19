package org.brukhman.ordermap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.hazelcast.core.Hazelcast;

/**
 * 
 * @author jbrukh
 *
 */
final class OrderState implements StateReader {

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
	OrderState(Map<UUID, Order> orders, Map<UUID, Execution> executions) {
		checkNotNull(orders);
		checkNotNull(executions);
		this.orders = orders;
		this.executions = executions;

		// TODO: must index here!
		this.order2exec = LinkedHashMultimap.create();
	}

	/**
	 * Create a new instance with empty state.
	 */
	public OrderState() {
		this(	
				Maps.<UUID, Order>newHashMap(), 
				Maps.<UUID, Execution>newHashMap()
				);
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#put(java.util.UUID, org.brukhman.ordermap.Execution)
	 */
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
	public final void deleteExecution(UUID orderId, UUID executionId) {
		deleteExecutions(orderId, Sets.newHashSet(executionId));
	}

	/* (non-Javadoc)
	 * @see org.brukhman.ordermap.State#deleteOrder(java.util.UUID)
	 */
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
