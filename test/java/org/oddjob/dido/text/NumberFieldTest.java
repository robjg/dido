package org.oddjob.dido.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.dido.DataPlanType;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;

public class NumberFieldTest extends TestCase {

	public static class Employee {
		
		private double salary;

		public Employee() {}
		
		Employee(double salary) {
			this.salary = salary;
		}
		
		public double getSalary() {
			return salary;
		}

		public void setSalary(double salary) {
			this.salary = salary;
		}
	}
	
	StandardArooaSession session = new StandardArooaSession();
	
	BeanBindingBean fruitBinding = new BeanBindingBean(); 
	{
		fruitBinding.setArooaSession(session);
		fruitBinding.setNode("employee");
		fruitBinding.setType(new SimpleArooaClass(Employee.class));
	}
	
	public void testReadWrite() throws ArooaConversionException {
		
		String EOL = System.getProperty("line.separator");
		
		String data = 
			"$17,000.00" + EOL +
			"$23,500.00" + EOL;
	
		DataPlanType definition = new DataPlanType();
		definition.setArooaSession(session);
		definition.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/text/NumberFormatExample.xml",
				getClass().getClassLoader()));
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan((DataPlan<StreamIn, ?, ?, ?>) definition.toValue());
		readJob.setBindings(0, fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		
		readJob.run();
		
		Object[] beans = readJob.getBeans();
		assertEquals(2, beans.length);
		
		Employee employee1 = (Employee) beans[0];
		assertEquals(17000.0, employee1.getSalary());
		
		Employee employee2 = (Employee) beans[1];
		assertEquals(23500.0, employee2.getSalary());
				
		DataWriteJob writeJob = new DataWriteJob();
		writeJob.setPlan((DataPlan<?, ?, StreamOut, ?>) definition.toValue());
		writeJob.setBindings(0, fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
	
}
