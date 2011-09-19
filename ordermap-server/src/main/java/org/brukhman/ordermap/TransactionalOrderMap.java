package org.brukhman.ordermap;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.google.common.base.Preconditions.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.ILock;
import com.hazelcast.core.ITopic;

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
	private final OrderState state;
	private final BlockingQueue<Modification> workQueue = 
			new ArrayBlockingQueue<Modification>(10000);
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final ITopic<Modification> modificationTopic;
	private final ILock distributedLock;
	
	// logging
	private final static Logger logger = LoggerFactory.getLogger(TransactionalOrderMap.class);
	
	
	/**
	 * Processes incoming modifications and broadcasts them
	 * over Hazelcast.
	 */
	private final Runnable workerRunnable = new Runnable() {
		@Override
		public void run() {
			while(!Thread.interrupted()) {
				try {
					Modification modification = workQueue.take();
					distributedLock.lock();
					try {
						modification.modify(state);
						logger.info("Applied modification: {}", modification);
						modificationTopic.publish(modification);
					} finally {
						distributedLock.unlock();
					}
					
				} catch (InterruptedException e) {}
			}
		}
		
	};
	
	/**
	 * Get the TOM.
	 * 
	 * @return
	 */
	public static final TransactionalOrderMap getInstance() {
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
		state = new MapBasedOrderState( 
							Hazelcast.<UUID, Order> getMap("orderMap"),
							Hazelcast.<UUID, Execution> getMap("executionMap")
							);
		
		modificationTopic = Hazelcast.getTopic("modificationTopic");
		distributedLock = Hazelcast.getLock("downloadLock");
		
		executor.execute(workerRunnable);
	}
	
	private final void queueUp(Modification modification) {
		// TODO: make sure it goes in...
		try {
			workQueue.put(modification);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a new order object.
	 * 
	 * @param order
	 */
	final void addOrder(Order order) {
		queueUp(new AddOrderModification(order));
	}
	
	/**
	 * Delete an order.
	 * 
	 * @param orderId
	 */
	final void deleteOrder(UUID orderId) {
		queueUp(new DeleteOrderModification(orderId));
	}
	
	/**
	 * Add an execution.
	 * 
	 * @param execution
	 */
	final void addExecution(Execution execution) {
		queueUp(new AddExecutionModification(execution));
	}
	
	/**
	 * Delete an executions.
	 * 
	 * @param executionId
	 */
	final void deleteExecution(UUID executionId) {
		Execution execution = state.getExecution(executionId);
		checkNotNull(executionId);
		queueUp(new DeleteExecutionsModification(execution.getOrderId(), executionId));
	}
	
	public List<Order> getOrders() {
		return state.getOrders();
	}

	public List<Execution> getExecutions() {
		return state.getExecutions();
	}

	public Order getOrder(UUID id) {
		return state.getOrder(id);
	}

	public Execution getExecution(UUID id) {
		return state.getExecution(id);
	}
}
