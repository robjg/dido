package org.oddjob.dido.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.dido.DataPlanType;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;

public class DateFieldTest extends TestCase {

	public static class Employee {
		
		private Date clockOut;

		public Employee() {}
		
		public Date getClockOut() {
			return clockOut;
		}

		public void setClockOut(Date clockOut) {
			this.clockOut= clockOut;
		}
	}
	
	StandardArooaSession session = new StandardArooaSession();
	
	BeanBindingBean fruitBinding = new BeanBindingBean(); 
	{
		fruitBinding.setArooaSession(session);
		fruitBinding.setNode("employee");
		fruitBinding.setType(Employee.class);
	}
	
	public void testReadWrite() throws ArooaConversionException {
		
		String EOL = System.getProperty("line.separator");
		
		String data = 
			"23/11/10 17:34" + EOL +
			"22/11/10 15:01" + EOL;
	
		DataPlanType definition = new DataPlanType();
		definition.setArooaSession(session);
		definition.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/text/DateFormatExample.xml",
				getClass().getClassLoader()));
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan((DataPlan<StreamIn, ?, ?, ?>) definition.toValue());
		readJob.setBindings(0, fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		
		readJob.run();
		
		Object[] beans = readJob.getBeans();
		assertEquals(2, beans.length);
		
		Employee employee1 = (Employee) beans[0];
		assertEquals("23/11/10 17:34", 
				new SimpleDateFormat("dd/MM/yy HH:mm").format(
						employee1.getClockOut()));
		
		Employee employee2 = (Employee) beans[1];
		assertEquals("22/11/10 15:01", 
				new SimpleDateFormat("dd/MM/yy HH:mm").format(
						employee2.getClockOut()));
				
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
