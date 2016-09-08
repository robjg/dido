package org.oddjob.dido.morph;

import junit.framework.TestCase;

public class MorphDefinitionBuilderTest extends TestCase {

	public void testBuild() {
		
		MorphDefinitionBuilder test = new MorphDefinitionBuilder();
		
		test.add("fruit", String.class);
		test.add("quantity", Integer.class);
		
		MorphDefinition definition = test.build();
		
		String[] names = definition.getNames();
		
		assertEquals("fruit", names[0]);
		assertEquals("quantity", names[1]);
		
		assertEquals(String.class, definition.typeOf("fruit"));
		assertEquals(Integer.class, definition.typeOf("quantity"));
	}
}
