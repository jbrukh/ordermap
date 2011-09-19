package org.brukhman.ordermap;

import static org.junit.Assert.*;

import org.junit.Test;

public class AddOrderModificationTest {
	
	private final static Order order = new Order("AAPL");
	
	@Test
	public void test() {
		MapBasedOrderState state = new MapBasedOrderState();
		
		new AddOrderModification(order).modify(state);
		assertTrue(state.orders.containsKey(order.getId()));
		assertTrue(state.orders.containsValue(order));
		assertTrue(state.executions.size()==0);
		assertTrue(state.order2exec.size()==0);
	}

}
