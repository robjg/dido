package org.oddjob.dido.match;

import java.util.ArrayList;
import java.util.List;

public class SharedTestData {

	List<Fruit> fruitX = new ArrayList<Fruit>();
	List<Fruit> fruitY = new ArrayList<Fruit>();
		
	
	public static class Fruit {
		
		private long id;
		private String type;
		private int quantity;
		private String colour;
		
		public long getId() {
			return id;
		}
		
		public void setId(long id) {
			this.id = id;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String name) {
			this.type = name;
		}
		
		public int getQuantity() {
			return quantity;
		}
		
		public void setQuantity(int age) {
			this.quantity = age;
		}
		
		public String getColour() {
			return colour;
		}
		
		public void setColour(String snack) {
			this.colour = snack;
		}
	}
	

	{		
		Fruit fruit = new Fruit();		
		fruit.setId(1);
		fruit.setType("Apple");
		fruit.setQuantity(4);
		fruit.setColour("green");
	
		fruitX.add(fruit);
	}
	
	{
		Fruit fruit = new Fruit();		
		fruit.setId(2);
		fruit.setType("Banana");
		fruit.setQuantity(3);
		fruit.setColour("yellow");
		
		fruitX.add(fruit);
	}
	
	{
		Fruit fruit = new Fruit();		
		fruit.setId(5);
		fruit.setType("Orange");
		fruit.setQuantity(2);
		fruit.setColour("orange");
		
		fruitX.add(fruit);
	}
	
	{
		Fruit fruit = new Fruit();		
		fruit.setId(1);
		fruit.setType("Apple");
		fruit.setQuantity(4);
		fruit.setColour("red");

		fruitY.add(fruit);
	}
	
	{
		Fruit fruit = new Fruit();		
		fruit.setId(2);
		fruit.setType("Banana");
		fruit.setQuantity(4);
		fruit.setColour("yellow");
		
		fruitY.add(fruit);
	}
	
	{
		Fruit fruit = new Fruit();		
		fruit.setId(3);
		fruit.setType("Orange");
		fruit.setQuantity(2);
		fruit.setColour("orange");		
		
		fruitY.add(fruit);
	}	
}
