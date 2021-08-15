package org.oddjob.dido.poi.test;

import java.util.Date;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;

/**
 * Test class for Data.
 * 
 * @author rob
 *
 */
public class Person {
	
	private String name;
	
	private Date dateOfBirth;
	
	private Double salary;
	
	public Person(String name,
			Date dateOfBirth,
			Double salery) {
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.salary = salery;
	}
	
	public Person() {
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	@ArooaAttribute
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public Double getSalary() {
		return salary;
	}
	
	public void setSalary(Double salery) {
		this.salary = salery;
	}
}
