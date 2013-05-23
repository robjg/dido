package org.oddjob.dido.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;

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
	
	BeanBindingBean employeeBinding = new BeanBindingBean(); 
	{
		employeeBinding.setArooaSession(session);
		employeeBinding.setNode("employee");
		employeeBinding.setType(new SimpleArooaClass(Employee.class));
	}
	
	public void testReadWrite() throws ArooaConversionException, IOException {
		
		String EOL = System.getProperty("line.separator");
		
		String data = 
			"$17,000.00" + EOL +
			"$23,500.00" + EOL;
	
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource("org/oddjob/dido/text/NumberFormatExample.xml");
		
		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan(layout);
		readJob.setBindings("employee", employeeBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		readJob.setBeans(new ArrayList<Object>());
		
		readJob.run();
		
		Object[] beans = readJob.getBeans().toArray();
		assertEquals(2, beans.length);
		
		Employee employee1 = (Employee) beans[0];
		assertEquals(17000.0, employee1.getSalary());
		
		Employee employee2 = (Employee) beans[1];
		assertEquals(23500.0, employee2.getSalary());
				
		DataWriteJob writeJob = new DataWriteJob();
		writeJob.setPlan(layout);
		writeJob.setBindings("employee", employeeBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setOutput(output);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
	
}
