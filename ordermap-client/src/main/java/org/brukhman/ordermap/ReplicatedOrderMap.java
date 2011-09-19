package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

/**
 * This is a hub for subscriptions to order-related
 * events. It is asynchronously replicated from TOM.
 * @author jbrukh
 *
 */
public final class ReplicatedOrderMap {

	private static ReplicatedOrderMap rom;
	private final static Object lock = new Object();
	private final static Logger logger = LoggerFactory.getLogger(ReplicatedOrderMap.class);
	
	private MapBasedOrderState orderState;
	
	/**
	 * Get the instance.
	 * 
	 * @return
	 */
	public final static ReplicatedOrderMap getInstance() {
		synchronized(lock) {
			if (rom == null) {
				rom = new ReplicatedOrderMap();
			}
			return rom;
		}
	}
	
	/**
	 * Create a new instance.
	 * 
	 */
	private ReplicatedOrderMap() {
		init();
	}
	
	/**
	 * Download the remote maps in a single transaction.
	 */
	private final void init() {
		// this is the shared memory
		IMap<UUID, Order> distributedOrders = Hazelcast.getMap("orderMap");
		IMap<UUID, Execution> distributedExecutions = Hazelcast.getMap("executionMap");

		logger.info("Downloading state...");
		// download all the orders
		ILock distributedLock = Hazelcast.getLock("downloadLock");
		distributedLock.lock();
		try {
			Map<UUID, Order> orders = Maps.newHashMap(distributedOrders);
			Map<UUID, Execution> executions = Maps.newHashMap(distributedExecutions);
			orderState = new MapBasedOrderState(orders, executions);
			
			// listen to modifications
			ITopic<Modification> topic = Hazelcast.getTopic("modificationTopic");
			topic.addMessageListener(modificationListener);
		} finally {
			distributedLock.unlock();
		}
		logger.info("Done...");
		for (Order order : orderState.getOrders()) {
			logger.info("Order: {}", order);
		}
	}

	private final MessageListener<Modification> modificationListener = 
			new MessageListener<Modification>() {
		@Override
		public void onMessage(Modification modification) {
			modification.modify(orderState);
		}
	};

}
