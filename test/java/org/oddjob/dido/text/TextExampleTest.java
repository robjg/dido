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
import org.oddjob.dido.stream.InputStreamIn;
import org.oddjob.dido.stream.OutputStreamOut;

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
		fruitBinding.setType(new SimpleArooaClass(Fruit.class));
	}
	
	public void testFixedReadWrite() throws ArooaConversionException, IOException {
		
		String EOL = System.getProperty("line.separator");
				
		String data = 
		//             1         2         3
		//   0123456789012345678901234567890123456789
			"Cox           Apple     Red         " + EOL +
			"Granny Smith  Apple     Green       " + EOL;
	
		ImportType importType = new ImportType();
		importType.setArooaSession(new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader())));
		importType.setResource("org/oddjob/dido/text/FixedWidthExample.xml");
		
		Layout layout = (Layout) importType.toObject();
				
		DataReadJob readJob = new DataReadJob();
		readJob.setPlan(layout);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setData(new InputStreamIn(
				new ByteArrayInputStream(data.getBytes())));
		readJob.setBeans(new ArrayList<Object>());
		
		readJob.run();
		
		Object[] beans = readJob.getBeans().toArray();
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
		writeJob.setPlan(layout);
		writeJob.setBindings("fruit", fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		writeJob.setData(new OutputStreamOut(output));
		
		writeJob.run();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
}
