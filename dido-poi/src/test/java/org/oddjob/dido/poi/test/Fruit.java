package org.oddjob.dido.poi.test;

import java.util.Date;

import org.oddjob.arooa.utils.DateHelper;

public class Fruit {

	private String fruit;
	
	private Date bestBefore;
	
	private String colour;
	
	private int quantity;
	
	private double price;

	public String getFruit() {
		return fruit;
	}

	public void setFruit(String type) {
		this.fruit = type;
	}

	public Date getBestBefore() {
		return bestBefore;
	}
	
	public void setBestBefore(Date bestBefore) {
		this.bestBefore = bestBefore;
	}
	
	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": fruit=" + fruit +
				", bestBefore" + DateHelper.formatDate(bestBefore) +
				", colour=" + colour + ", quantity=" + quantity + 
				", price=" + price;
	}
}
