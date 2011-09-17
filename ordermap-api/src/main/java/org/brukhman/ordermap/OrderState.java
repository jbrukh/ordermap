package org.brukhman.ordermap;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.SetMultimap;

final class OrderState {

	// main data objects
	Map<UUID, Order> 		orders;
	Map<UUID, Execution> 	executions;
	
	// indices on the data objects that define
	// child relationships
	SetMultimap<UUID, UUID> order2exec;
}
