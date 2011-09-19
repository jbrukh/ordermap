package org.brukhman.ordermap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerNode {
	
	private final static Logger logger = LoggerFactory.getLogger(ServerNode.class);
	
	public final static void main(String... args) {
		logger.info("Starting...");
		TransactionalOrderMap tom = TransactionalOrderMap.getInstance();
		
		tom.addOrder( new Order("AAPL") );
		tom.addOrder( new Order("MSFT") );
		tom.addOrder( new Order("MMM") );
		
	}

}
