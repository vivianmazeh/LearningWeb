package com.learningWeb.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class Book {
	
	//export ECLIPSE_HOME=/Applications/SpringToolSuite4
	
	private int id;
	private String name;	
	private double price;
	
	public Book(String name, int id, double price) {
		
		this.setName(name);
		this.setId(id);
		this.setPrice(price);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
