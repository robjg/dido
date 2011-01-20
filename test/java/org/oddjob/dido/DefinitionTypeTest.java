package org.oddjob.dido;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.dido.stream.Lines;

public class DefinitionTypeTest extends TestCase {

	public void testDefinitionToNode() throws ArooaConversionException {
		
		String xml = 
			"<lines>" +
			" <is>" +
			"  <delimited>" +
			"   <is>" +
			"    <field name='name'/>" +
			"    <field name='age'/>" +
			"    <field name='city'/>" +
			"   </is>" +
			"  </delimited>" +
			" </is>" +
			"</lines>";
		
		DataPlanType test = new DataPlanType();
		test.setConfiguration(new XMLConfiguration("XML", xml));
		test.setArooaSession(new StandardArooaSession());
		
		DataPlan<?, ?, ?, ?> origin = test.toValue();
		DataNode<?, ?, ?, ?> node = origin.getTopNode();
		
		assertNotNull(node);
		
		assertEquals(Lines.class, node.getClass());
	}
}
