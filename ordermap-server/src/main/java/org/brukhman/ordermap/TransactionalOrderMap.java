package org.brukhman.ordermap;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static com.google.common.base.Preconditions.*;
import com.google.common.collect.LinkedHashMultimap;
import com.hazelcast.core.Hazelcast;

/**
 * The {@link TransactionalOrderMap} provides the interface
 * to data object modification. It stores orders in a distributed
 * cache, and multiple instances of the TransactionalOrderMap
 * can process requests simultaneously.
 * 
 * @author jbrukh
 *
 */
public final class TransactionalOrderMap {
	
	// tom instance
	private static TransactionalOrderMap tom;
	private final static Object lock = new Object();
	
	// where the data is stored
	private final OrderState state = new OrderState();
	private final BlockingQueue<Modification> broadcastQueue = 
			new ArrayBlockingQueue<Modification>(10000);
	
	// listeners
	private final CopyOnWriteArrayList<Modification.Listener> listeners = 
			new CopyOnWriteArrayList<Modification.Listener>();
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * Get the TOM.
	 * 
	 * @return
	 */
	public final TransactionalOrderMap getInstance() {
		synchronized(lock) {
			if (tom == null) {
				tom = new TransactionalOrderMap();
			}
			return tom;
		}
	}
	
	/**
	 * Create a TOM.
	 * 
	 */
	private TransactionalOrderMap() {
		state.orders = Hazelcast.getMap("orderMap");
		state.executions = Hazelcast.getMap("executionMap");
		
		// TODO: synchronization?
		state.order2exec = LinkedHashMultimap.create(); // map orderId => execIds	
		for (Execution execution : state.executions.values()) {
			state.order2exec.put(execution.getOrderId(), execution.getId());
		}
		
		// start a separate thread that broadcasts modifications to
		// listeners for asynchronous replication
		executor.execute(broadcastRunnable);
	}

	
	/**
	 * Resolve an order.
	 * 
	 * @param id
	 * @return
	 */
	public final Order getOrder(UUID id) {
		// this goes straight for the data; since
		// this is the server-side the data should
		// be available locally
		return state.orders.get(id);
	}
	
	/**
	 * Resolve an execution.
	 * 
	 * @param id
	 * @return
	 */
	public final Execution getExecution(UUID id) {
		return state.executions.get(id);
	}
	
	/**
	 * Add a new order object.
	 * 
	 * @param order
	 */
	final void addOrder(Order order) {
		applyAndBroadcast(
				new AddOrderModification(order)
				);
	}
	
	/**
	 * Delete an order.
	 * 
	 * @param orderId
	 */
	final void deleteOrder(UUID orderId) {
		applyAndBroadcast(
				new DeleteOrderModification(orderId)
				);
	}
	
	/**
	 * Add an execution.
	 * 
	 * @param execution
	 */
	final void addExecution(Execution execution) {
		applyAndBroadcast(
				new AddExecutionModification(execution)
				);
	}
	
	/**
	 * Delete an executions.
	 * 
	 * @param executionId
	 */
	final void deleteExecution(UUID executionId) {
		Execution execution = state.executions.get(executionId);
		checkNotNull(executionId);
		
		applyAndBroadcast(
				new DeleteExecutionsModification(execution.getOrderId(), executionId)
				);
	}

	/**
	 * Apply the modification and broadcast it.
	 * 
	 * @param modification
	 */
	private final void applyAndBroadcast(Modification modification) {
		modification.actOn(state);
		broadcastQueue.add(modification);
	}
	
	/**
	 * Add a listener for modifications.
	 * 
	 * @param listener
	 */
	public final void addListener(Modification.Listener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove a listener for modifications.
	 * 
	 * @param listener
	 */
	public final void removeListener(Modification.Listener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * 
	 */
	private final Runnable broadcastRunnable = new Runnable() {
		public void run() {
			
			while(!Thread.interrupted()) {
				try {
					Modification modification = broadcastQueue.take();
					for (Modification.Listener listener : listeners) {
						listener.modificationOccurred(modification);
					}
				} catch (InterruptedException e) {}
			}
			
		}
	};
}
