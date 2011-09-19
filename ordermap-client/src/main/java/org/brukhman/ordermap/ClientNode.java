package org.brukhman.ordermap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientNode {
	
	private final static Logger logger = LoggerFactory.getLogger(ClientNode.class);
	
	public final static void main(String... args) {
		logger.info("Starting client...");
		ReplicatedOrderMap rom = ReplicatedOrderMap.getInstance();

		
	}

}