package org.brukhman.ordermap;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for orders.
 * 
 * @author jbrukh
 *
 */
public class OrderTest {

	@Test
	public void testCopy() {
		Order order = new Order("AAPL");
		assertTrue(!order.isDeleted());
		assertNotNull(order.getId());
		
		Order copy = new Order(order);
		assertTrue(!copy.isDeleted());
		assertNotNull(copy.getId());
		
	}

}
