package org.oddjob.dido;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;

public class WriteJobTest extends TestCase {

	String EOL = System.getProperty("line.separator");
	
	public void testInOddjob() throws ArooaPropertyException, ArooaConversionException {
		
		Person person1 = new Person();
		person1.setName("John");
		person1.setAge("24");
		person1.setCity("London");
		
		Person person2 = new Person();
		person2.setName("Fred");
		person2.setAge("57");
		person2.setCity("Paris");
		
		Person person3 = new Person();
		person3.setName("Jane");
		person3.setAge("32");
		person3.setCity("New York");
		
		List<Object> beans = new ArrayList<Object>();
		beans.add(person1);
		beans.add(person2);
		beans.add(person3);
				
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/WriteJobExample.xml", 
				getClass().getClassLoader()));
		
		oddjob.setExport("beans", new ArooaObject(beans));
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		String data =
			"John,24,London" + EOL +
			"Fred,57,Paris" + EOL +
			"Jane,32,New York" + EOL;
		
		String results = new OddjobLookup(oddjob).lookup("results", String.class);

		assertEquals(data, results);		
	}
}
