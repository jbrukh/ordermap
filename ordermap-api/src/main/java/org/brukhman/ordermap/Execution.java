package org.brukhman.ordermap;

import java.math.BigDecimal;
import java.util.UUID;

public final class Execution {

	private final UUID id;
	private final UUID orderId;
	private final int quantity;
	private final BigDecimal price;
	
	/**
	 * Create a new instance.
	 * @param orderId
	 * @param quantity
	 * @param price
	 */
	public Execution(UUID orderId, int quantity, BigDecimal price) {
		this.orderId = orderId;
		this.quantity = quantity;
		this.price = price;
		this.id = UUID.randomUUID();
	}

	/**
	 * @return the id
	 */
	public final UUID getId() {
		return id;
	}

	/**
	 * @return the orderId
	 */
	public final UUID getOrderId() {
		return orderId;
	}

	/**
	 * @return the quantity
	 */
	public final int getQuantity() {
		return quantity;
	}

	/**
	 * @return the price
	 */
	public final BigDecimal getPrice() {
		return price;
	}
	
}
