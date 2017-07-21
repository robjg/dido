package org.oddjob.dido.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ImportType;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.BeanBindingBean;
import org.oddjob.dido.stream.IOStreamData;

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
	
	public void testFixedReadWrite() throws ArooaConversionException, IOException, DataException {
		
		String EOL = System.getProperty("line.separator");
				
		String data = 
		//             1         2         3
		//   0123456789012345678901234567890123456789
			"Cox           Apple     Red         " + EOL +
			"Granny Smith  Apple     Green       " + EOL;
	
		ArooaSession session = new StandardArooaSession(
				new ClassPathDescriptorFactory(
						).createDescriptor(getClass().getClassLoader()));
		
		ImportType importType = new ImportType();
		importType.setArooaSession(session);
		importType.setResource("org/oddjob/dido/text/FixedWidthExample.xml");
		
		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(new StandardArooaSession());
		ioData.setInput(new ArooaObject(
				new ByteArrayInputStream(data.getBytes())));
		
		Layout layout = (Layout) importType.toObject();
				
		DataReadJob readJob = new DataReadJob();
		readJob.setArooaSession(session);
		readJob.setLayout(layout);
		readJob.setBindings("fruit", fruitBinding);
		readJob.setData(ioData);
        List<Object> resultBeans = new ArrayList<Object>();
		readJob.setBeans(resultBeans);
		
		readJob.call();
		
		Object[] beans = resultBeans.toArray();
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
		writeJob.setArooaSession(session);
		writeJob.setLayout(layout);
		writeJob.setBindings("fruit", fruitBinding);		
		
		writeJob.setBeans(Arrays.asList(beans));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();		
		ioData.setOutput(new ArooaObject(output));
		
		writeJob.setData(ioData);
		
		writeJob.call();
		
		assertEquals(data, new String(output.toByteArray()));
		
	}
}
