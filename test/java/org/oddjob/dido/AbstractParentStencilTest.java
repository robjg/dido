package org.oddjob.dido;

import junit.framework.TestCase;

import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextOut;

public class AbstractParentStencilTest extends TestCase {
	
	public void testTypesOfChild() {
		
		class Ours extends AbstractParentStencil
		<String, DataIn, StreamIn, DataOut, StreamOut> {
			public Class<String> getType() {
				return String.class;
			}

			@Override
			public WhereNextIn<StreamIn> in(
					DataIn data) throws DataException {
				throw new RuntimeException("Unexpected.");
			}

			@Override
			public WhereNextOut<StreamOut> out(
					DataOut data) throws DataException {
				throw new RuntimeException("Unexpected.");
			}
			
			public String getValue() {
				return null;
			}
			
			public void setValue(String value) {
			}
		}
		
		class Node extends MockDataNode<StreamIn, TextIn, StreamOut, TextOut> {
			
			@Override
			public String getName() {
				throw new RuntimeException("Unexpected");
			}
			
			@Override
			public WhereNextIn<TextIn> in(
					StreamIn data) throws DataException {
				throw new RuntimeException("Unexpected");
			}
			
			@Override
			public WhereNextOut<TextOut> out(
					StreamOut outgoing) throws DataException {
				throw new RuntimeException("Unexpected");
			}
		}
		
		Ours test = new Ours();
		
		test.setIs(0, new Node());
		
		DataNode<?, ?, ?, ?> node = test.getIs(0);
		
		assertNotNull(node);
	}	
}
