package org.oddjob.dido;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;

public class DataReadJobTest extends TestCase {

	public void testInOddjob() throws ArooaPropertyException, ArooaConversionException {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/ReadJobExample.xml", 
				getClass().getClassLoader()));
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		Object[] results = new OddjobLookup(oddjob).lookup("read.beans", Object[].class);

		assertEquals(3, results.length);
		
		Person person1 = (Person) results[0];
		assertNotNull(person1);
		assertEquals(person1.getName(), "John");
		assertEquals(person1.getAge(), "24");
		assertEquals(person1.getCity(), "London");
		
		Person person2 = (Person) results[1];
		assertNotNull(person2);
		assertEquals(person2.getName(), "Fred");
		assertEquals(person2.getAge(), "57");
		assertEquals(person2.getCity(), "Paris");
		
		Person person3 = (Person) results[2];
		assertNotNull(person3);
		assertEquals(person3.getName(), "Jane");
		assertEquals(person3.getAge(), "32");
		assertEquals(person3.getCity(), "New York");
	}
}
