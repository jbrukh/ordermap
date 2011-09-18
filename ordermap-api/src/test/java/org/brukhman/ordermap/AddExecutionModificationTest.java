package org.brukhman.ordermap;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class AddExecutionModificationTest {

	private final static Order order = new Order("AAPL");
	private final static Execution execution = new Execution(order.getId(), 100, 
									BigDecimal.valueOf(200.00d));
	private final OrderState state = OrderState.emptryOrderState();

	@Before
	public void setUp() throws Exception {
		state.orders.put(order.getId(), order);
	}

	@Test(expected=IllegalStateException.class)
	public void testForeignExecution() {
		// different order
		Execution foreign = new Execution(UUID.randomUUID(), 100, 
				BigDecimal.valueOf(200.00d));
		new AddExecutionModification(foreign).actOn(state);
	}
	
	@Test
	public void testAdd() {
		new AddExecutionModification(execution).actOn(state);
		assertTrue(state.orders.size()==1);
		assertTrue(state.executions.size()==1);
		assertTrue(state.executions.containsKey(execution.getId()));
		assertTrue(state.executions.containsValue(execution));
		assertTrue(state.order2exec.get(order.getId()).contains(execution.getId()));
	}

}
