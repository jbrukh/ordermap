package org.brukhman.ordermap;

import java.io.Serializable;
import java.util.UUID;

import com.google.common.base.Preconditions;

/**
 * An order.
 * 
 * @author jbrukh
 *
 */
public final class Order implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2215435082862019293L;
	
	private final String security;
	private final UUID id;
	
	/** package-accessible flag for deletion */
	boolean isDeleted;
	

	/**
	 * Create a new order.
	 * 
	 * @param security
	 */
	public Order(String security) {
		Preconditions.checkNotNull(security);
		this.security = security;
		this.id = UUID.randomUUID();
		this.isDeleted = false;
	}

	Order(Order order) {
		Preconditions.checkNotNull(order);
		this.security = order.security;
		this.id = order.id;
		this.isDeleted = order.isDeleted;
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
	
	/**
	 * Returns whether this order is marked as deleted.
	 * 
	 * @return
	 */
	public final boolean isDeleted() {
		return isDeleted;
	}
	
	public String toString() {
		return id.toString() + "/" + security;
	}
}
