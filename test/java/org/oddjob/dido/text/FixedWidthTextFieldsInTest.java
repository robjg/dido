package org.oddjob.dido.text;

import org.oddjob.dido.DataException;
import org.oddjob.dido.tabular.ColumnIn;

import junit.framework.TestCase;

public class FixedWidthTextFieldsInTest extends TestCase {

	private class OurFixedWidthColumn
	implements FixedWidthColumn {
		
		private final int index;
		private final int length;
		
		public OurFixedWidthColumn(int index, int length) {
			this.index = index;
			this.length = length;
		}
		
		@Override
		public int getIndex() {
			return index;
		}
		
		@Override
		public int getLength() {
			return length;
		}
		
		@Override
		public String getLabel() {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	/**
	 * This happens with Case.
	 * @throws DataException 
	 */
	public void testTwoTextFieldsInSamePosition() throws DataException {
		
		FixedWidthTextFieldsIn test = new FixedWidthTextFieldsIn();
		test.setText("Apples");
		
		ColumnIn<String> col1 = test.inFor(new OurFixedWidthColumn(1, 10));
		ColumnIn<String> col2 = test.inFor(new OurFixedWidthColumn(1, 10));
		
		assertEquals("Apples", col1.getData());
		assertEquals("Apples", col2.getData());
	}
	
}
