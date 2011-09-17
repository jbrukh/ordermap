package org.brukhman.ordermap;

import java.util.UUID;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;

/**
 * This is a hub for subscriptions to order-related
 * events. It is asynchronously replicated from TOM.
 * @author jbrukh
 *
 */
public final class ReplicatedOrderMap {

	private static ReplicatedOrderMap rom;
	private final static Object lock = new Object();
	
	public final ReplicatedOrderMap getInstance() {
		synchronized(lock) {
			if (rom == null) {
				rom = new ReplicatedOrderMap();
			}
			return rom;
		}
	}
	
	private ReplicatedOrderMap() {
		IMap<UUID, Order> orders = Hazelcast.getMap("orderMap");
		IMap<UUID, Execution> executions = Hazelcast.getMap("executionMap");
		
		orders.addEntryListener(orderMapListener, false);
		executions.addEntryListener(executionMapListener, false);
	}
	
	/**
	 * Listener for the order map.
	 */
	private final static EntryListener<UUID, Order> orderMapListener = new EntryListener<UUID, Order>() {

		public void entryAdded(EntryEvent<UUID, Order> event) {
			// TODO Auto-generated method stub
			
		}

		public void entryRemoved(EntryEvent<UUID, Order> event) {
			// TODO Auto-generated method stub
			
		}

		public void entryUpdated(EntryEvent<UUID, Order> event) {
			// TODO Auto-generated method stub
			
		}

		public void entryEvicted(EntryEvent<UUID, Order> event) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * Listener for the order map.
	 */
	private final static EntryListener<UUID, Execution> executionMapListener = new EntryListener<UUID, Execution>() {

		public void entryAdded(EntryEvent<UUID, Execution> event) {
			// TODO Auto-generated method stub
			
		}

		public void entryRemoved(EntryEvent<UUID, Execution> event) {
			// TODO Auto-generated method stub
			
		}

		public void entryUpdated(EntryEvent<UUID, Execution> event) {
			// TODO Auto-generated method stub
			
		}

		public void entryEvicted(EntryEvent<UUID, Execution> event) {
			// TODO Auto-generated method stub
			
		}

	};
	
}
