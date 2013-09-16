package org.oddjob.dido.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.stream.IOStreamData;

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
		employeeBinding.setType(new SimpleArooaClass(Employee.class));
	}
	
	public void testReadWrite() throws ArooaConversionException, IOException {
		
		String EOL = System.getProperty("line.separator");
		
		String data = 
			"$17,000.00" + EOL +
			"$23,500.00" + EOL;
	
		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader()));
		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setResource("org/oddjob/dido/text/NumberFormatExample.xml");
		
		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(session);
		ioData.setInput(new ArooaObject(
				new ByteArrayInputStream(data.getBytes())));
		
		Layout layout = (Layout) importType.toObject();
		
		DataReadJob readJob = new DataReadJob();
		readJob.setLayout(layout);
		readJob.setBindings("employee", employeeBinding);
		readJob.setData(ioData);
		readJob.setBeans(new ArrayList<Object>());
		
		readJob.run();
		
		Object[] beans = readJob.getBeans().toArray();
		assertEquals(2, beans.length);
		
		Employee employee1 = (Employee) beans[0];
		assertEquals(17000.0, employee1.getSalary());
		
		Employee employee2 = (Employee) beans[1];
		assertEquals(23500.0, employee2.getSalary());
				
		DataWriteJob writeJob = new DataWriteJob();
		writeJob.setLayout(layout);
		writeJob.setBindings("employee", employeeBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();		
		ioData.setOutput(new ArooaObject(output));
		
		writeJob.setData(ioData);
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
	
}
