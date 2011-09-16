package org.brukhman.ordermap;

import java.util.UUID;

import com.google.common.base.Preconditions;

/**
 * An order.
 * 
 * @author jbrukh
 *
 */
public final class Order {
	
	private final String security;
	private final UUID id;
	

	/**
	 * Create a new order.
	 * 
	 * @param security
	 */
	public Order(String security) {
		Preconditions.checkNotNull(security);
		this.security = security;
		this.id = UUID.randomUUID();
	}

	/**
	 * @return the security
	 */
	public final String getSecurity() {
		return security;
	}
	
	/**
	 * @return the id
	 */
	public final UUID getId() {
		return id;
	}
}
