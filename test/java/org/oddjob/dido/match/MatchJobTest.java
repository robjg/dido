package org.oddjob.dido.match;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.JobState;

public class MatchJobTest extends TestCase {

	SharedTestData data = new SharedTestData();
	
	String EOL = System.getProperty("line.separator");
	
	public String expectedKeysDifferent =
		"matchResultType  id  xType   yType   typeComparison  xQuantity  yQuantity  quantityComparison  xColour  yColour  colourComparison" + EOL +
		"---------------  --  ------  ------  --------------  ---------  ---------  ------------------  -------  -------  ----------------" + EOL +
		"NOT_EQUAL        1   Apple   Apple                   4          4                              green    red      green<>red" + EOL +
		"NOT_EQUAL        2   Banana  Banana                  3          4          1.0 (33.3%)         yellow   yellow   " + EOL +
		"X_MISSING        3           Orange                             2                                       orange   " + EOL +
		"Y_MISSING        5   Orange                          2                                         orange            " + EOL;

	public String expectedKeysSame =
		"matchResultType  type    xQuantity  yQuantity  quantityComparison  xColour  yColour" + EOL +
		"---------------  ------  ---------  ---------  ------------------  -------  -------" + EOL +
		"EQUAL            Apple   4          4                              green    red" + EOL +
		"NOT_EQUAL        Banana  3          4          1.0 (33.3%)         yellow   yellow" + EOL +
		"EQUAL            Orange  2          2                              orange   orange" + EOL;

	
	public void testUnsortedKeysDifferent() throws Exception {
							
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/match/MatchExample1.xml", 
				getClass().getClassLoader()));
		oddjob.setExport("listX", new ArooaObject(data.fruitX));
		oddjob.setExport("listY", new ArooaObject(data.fruitY));
		oddjob.setExport("sorted", new ArooaObject(false));
		
		oddjob.run();
		
		assertEquals(JobState.COMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		String results = new OddjobLookup(
				oddjob).lookup("results", String.class);
		
		assertEquals(expectedKeysDifferent, results);
	}
	
	public void testUnsortedKeysSame() throws Exception {
						
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/match/MatchExample2.xml", 
				getClass().getClassLoader()));
		oddjob.setExport("listX", new ArooaObject(data.fruitX));
		oddjob.setExport("listY", new ArooaObject(data.fruitY));
		oddjob.setExport("sorted", new ArooaObject(false));
		
		oddjob.run();
		
		assertEquals(JobState.COMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		String results = new OddjobLookup(
				oddjob).lookup("results", String.class);
				
		assertEquals(expectedKeysSame, results);
	}
	
	public void testSortedKeysDifferent() throws Exception {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/match/MatchExample1.xml", 
				getClass().getClassLoader()));
		oddjob.setExport("listX", new ArooaObject(data.fruitX));
		oddjob.setExport("listY", new ArooaObject(data.fruitY));
		oddjob.setExport("sorted", new ArooaObject(true));
		
		oddjob.run();
		
		assertEquals(JobState.COMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		String results = new OddjobLookup(
				oddjob).lookup("results", String.class);
		
		assertEquals(expectedKeysDifferent, results);
	}
	
	public void testSortedKeysSame() throws Exception {
						
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/dido/match/MatchExample2.xml", 
				getClass().getClassLoader()));
		oddjob.setExport("listX", new ArooaObject(data.fruitX));
		oddjob.setExport("listY", new ArooaObject(data.fruitY));
		oddjob.setExport("sorted", new ArooaObject(true));
		
		oddjob.run();
		
		assertEquals(JobState.COMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		String results = new OddjobLookup(
				oddjob).lookup("results", String.class);
				
		assertEquals(expectedKeysSame, results);
	}
}
