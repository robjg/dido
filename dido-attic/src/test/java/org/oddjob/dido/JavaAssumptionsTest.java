package org.oddjob.dido;

import junit.framework.TestCase;

public class JavaAssumptionsTest extends TestCase {

	interface Foo {
		
		String foo();
	}
	
	Foo foo;
	
	Foo fooFor(final String text) {
		
		if (foo == null) {
			foo = new Foo() {
				@Override
				public String foo() {
					return text;
				}
			};
		}
		
		return foo;
	}
	
	public void testFinalVariablesAndNestedClasses() {
		
		Foo foo = fooFor("Apples");

		assertEquals("Apples", foo.foo());
		
		foo = fooFor("Pears");
		
		assertEquals("Apples", foo.foo());
	}
	
}
