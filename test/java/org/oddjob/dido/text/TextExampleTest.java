package org.oddjob.dido.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.dido.DataPlan;
import org.oddjob.dido.DataPlanType;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;

public class TextExampleTest extends TestCase {

	public static class Fruit {
		
		private String variety;
		private String type;
		private String colour;
		
		public String getVariety() {
			return variety;
		}
		public void setVariety(String variety) {
			this.variety = variety;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getColour() {
			return colour;
		}
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	StandardArooaSession session = new StandardArooaSession();
		
	BeanBindingBean fruitBinding = new BeanBindingBean(); 
	{
		fruitBinding.setArooaSession(session);
		fruitBinding.setNode("fruit");
		fruitBinding.setType(Fruit.class);
	}
	
	public void testFixedReadWrite() throws ArooaConversionException {
		
		String EOL = System.getProperty("line.separator");
				
		String data = 
		//             1         2         3
		//   0123456789012345678901234567890123456789
			"Cox           Apple     Red         " + EOL +
			"Granny Smith  Apple     Green       " + EOL;
	
		DataPlanType definition = new DataPlanType();
		definition.setArooaSession(session);
		definition.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/text/FixedWidthExample.xml",
				getClass().getClassLoader()));
		
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan((DataPlan<StreamIn, ?, ?, ?>) definition.toValue());
		readJob.setBindings(0, fruitBinding);
		readJob.setInput(new ByteArrayInputStream(data.getBytes()));
		
		readJob.run();
		
		Object[] beans = readJob.getBeans();
		assertEquals(2, beans.length);
		
		Fruit fruit1 = (Fruit) beans[0];
		assertEquals("Cox", fruit1.getVariety());
		assertEquals("Apple", fruit1.getType());
		assertEquals("Red", fruit1.getColour());
		
		Fruit fruit2 = (Fruit) beans[1];
		assertEquals("Granny Smith", fruit2.getVariety());
		assertEquals("Apple", fruit2.getType());
		assertEquals("Green", fruit2.getColour());
				
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
