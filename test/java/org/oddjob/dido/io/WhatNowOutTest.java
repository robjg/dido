package org.oddjob.dido.io;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataDriver;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.MockBoundedDataNode;
import org.oddjob.dido.MockDataNode;
import org.oddjob.dido.MockDataOut;
import org.oddjob.dido.WhereNextOut;


public class WhatNowOutTest extends TestCase {
	private static final Logger logger = Logger.getLogger(WhatNowOutTest.class);
	
	class Root extends MockBoundedDataNode<DataIn, DataIn, DataOut, DataOut> {
		Middle child = new Middle();
		
		@Override
		public WhereNextOut<DataOut> out(
				DataOut outgoing) throws DataException {
			
			Middle[] children = { child };
			
			return new WhereNextOut<DataOut>(children, 
					new MockDataOut());
		}

		@Override
		public void flush(DataOut data, DataOut childData) throws DataException {
			results.add("Root flush.");
		}
	}
	
	class Middle extends MockDataNode<DataIn, DataIn, DataOut, DataOut> 
	implements DataDriver {
		
		int count;
		
		Leaf[] children = { new Leaf("One"), new Leaf("Two") };
		
		@Override
		public WhereNextOut<DataOut> out(
				DataOut outgoing) throws DataException {
			
			if (count++ == 0) {
				results.add("Middle starting.");
				
				for (Leaf leaf : children) {
					leaf.begin(new MockDataOut());
				}
			}
			
			return new WhereNextOut<DataOut>(children,
					new MockDataOut());
		}
		
		@Override
		public void flush(DataOut data, DataOut childData) throws DataException {
			results.add("Middle flush.");
		}
		
		@Override
		public void complete(DataOut out) throws DataException {
			for (Leaf leaf : children) {
				leaf.end(new MockDataOut());
			}
			
			results.add("Middle complete.");
		}
		
		@Override
		public String toString() {
			return "Middle";
		}
	}
	
	class Leaf extends MockBoundedDataNode<DataIn, DataIn, DataOut, DataOut> {
		
		String name;
		
		Leaf(String name) {
			this.name = name;
		}
		
		@Override
		public void begin(DataOut out) {
			results.add("Leaf " + name + " begin.");
		}
		
		@Override
		public void end(DataOut out) {
			results.add("Leaf " + name + " end.");
		}
		
		@Override
		public WhereNextOut<DataOut> out(
				DataOut outgoing) throws DataException {
			return new WhereNextOut<DataOut>();
		}
		
		@Override
		public void complete(DataOut out) throws DataException {
			results.add("Leaf " + name + " complete.");
		}
		
		@Override
		public String toString() {
			return "Leaf " + name;
		}
	}
	
	List<String> results = new ArrayList<String>();
	
	public void testFullCycle() throws DataException {
		
		Root root = new Root();
		
		DataOut rootData = new MockDataOut();
		
		WhatNowOut rootWhatNow = new WhatNowOut(root, rootData);
		
		assertEquals(WhatNowOut.State.NEW, rootWhatNow.getState());
		assertEquals(root, rootWhatNow.getCurrent());
		
		assertEquals(0, results.size());
		
		assertTrue(rootWhatNow.advance(
				WhatNowOut.Navigation.VISIT));
		
		assertEquals(WhatNowOut.State.CHILDREN, rootWhatNow.getState());

		assertEquals(0, results.size());
		
		WhatNowOut middleWhatNow = rootWhatNow.nextChild();
		
		assertEquals(WhatNowOut.State.NEW, middleWhatNow.getState());
		assertEquals("Middle", middleWhatNow.getCurrent().toString());
		
		assertTrue(middleWhatNow.advance(
				WhatNowOut.Navigation.VISIT));
		
		assertEquals(WhatNowOut.State.CHILDREN, middleWhatNow.getState());
		assertEquals("Middle", middleWhatNow.getCurrent().toString());
		
		assertEquals(3, results.size());
		assertEquals("Middle starting.", results.get(0));
		assertEquals("Leaf One begin.", results.get(1));
		assertEquals("Leaf Two begin.", results.get(2));
		
		WhatNowOut leaf1OneWhatNow = middleWhatNow.nextChild();
				
		assertEquals(WhatNowOut.State.NEW, leaf1OneWhatNow.getState());
		assertEquals("Leaf One", leaf1OneWhatNow.getCurrent().toString());
		
		assertTrue(leaf1OneWhatNow.advance(
				WhatNowOut.Navigation.VISIT));
		
		assertEquals(WhatNowOut.State.PROCESSED, leaf1OneWhatNow.getState());
		assertEquals("Leaf One", leaf1OneWhatNow.getCurrent().toString());
		
		assertFalse(leaf1OneWhatNow.after());
				
		WhatNowOut leaf1TwoWhatNow = middleWhatNow.nextChild();
		
		assertEquals(WhatNowOut.State.NEW, leaf1TwoWhatNow.getState());
		assertEquals("Leaf Two", leaf1TwoWhatNow.getCurrent().toString());
		
		assertTrue(leaf1TwoWhatNow.advance(
				WhatNowOut.Navigation.VISIT));
		
		assertEquals(WhatNowOut.State.PROCESSED, leaf1TwoWhatNow.getState());
		assertEquals("Leaf Two", leaf1TwoWhatNow.getCurrent().toString());
		
		assertFalse(leaf1TwoWhatNow.after());
		
		assertNull(middleWhatNow.nextChild());
						
		assertEquals(WhatNowOut.State.PROCESSED, middleWhatNow.getState());
		assertEquals("Middle", middleWhatNow.getCurrent().toString());
		
		assertEquals("Middle flush.", results.get(3));
		
		assertTrue(middleWhatNow.after());
		
		assertEquals(WhatNowOut.State.NEW, middleWhatNow.getState());
		assertEquals("Middle", middleWhatNow.getCurrent().toString());
		
		assertTrue(middleWhatNow.advance(
				WhatNowOut.Navigation.VISIT));
		
		assertEquals(4, results.size());
		
		assertEquals(WhatNowOut.State.CHILDREN, middleWhatNow.getState());
		assertEquals("Middle", middleWhatNow.getCurrent().toString());
		
		WhatNowOut leaf2OneWhatNow = middleWhatNow.nextChild();
				
		assertEquals(WhatNowOut.State.NEW, leaf2OneWhatNow.getState());
		assertEquals("Leaf One", leaf2OneWhatNow.getCurrent().toString());
		
		assertTrue(leaf2OneWhatNow.advance(
				WhatNowOut.Navigation.VISIT));
		
		assertEquals(WhatNowOut.State.PROCESSED, leaf2OneWhatNow.getState());
		assertEquals("Leaf One", leaf2OneWhatNow.getCurrent().toString());
		
		assertFalse(leaf2OneWhatNow.after());
				
		WhatNowOut leaf2TwoWhatNow = middleWhatNow.nextChild();
		
		assertEquals(WhatNowOut.State.NEW, leaf2TwoWhatNow.getState());
		assertEquals("Leaf Two", leaf2TwoWhatNow.getCurrent().toString());
		
		assertTrue(leaf2TwoWhatNow.advance(
				WhatNowOut.Navigation.VISIT));
		
		assertEquals(WhatNowOut.State.PROCESSED, leaf2TwoWhatNow.getState());
		assertEquals("Leaf Two", leaf2TwoWhatNow.getCurrent().toString());
		
		assertFalse(leaf2TwoWhatNow.after());
		
		assertNull(middleWhatNow.nextChild());
						
		assertEquals(WhatNowOut.State.PROCESSED, middleWhatNow.getState());
		assertEquals("Middle", middleWhatNow.getCurrent().toString());
		
		assertEquals("Middle flush.", results.get(4));
		
		assertTrue(middleWhatNow.after());
		
		assertEquals(WhatNowOut.State.NEW, middleWhatNow.getState());
		assertEquals("Middle", middleWhatNow.getCurrent().toString());
		
		assertEquals(5, results.size());
		
		assertFalse(middleWhatNow.advance(
				WhatNowOut.Navigation.COMPLETE));
		
		assertEquals("Leaf One complete.", results.get(5));
		assertEquals("Leaf Two complete.", results.get(6));
		assertEquals("Leaf One end.", results.get(7));
		assertEquals("Leaf Two end.", results.get(8));
		assertEquals("Middle complete.", results.get(9));
		
		assertEquals(10, results.size());
		
		assertNull(rootWhatNow.nextChild());
		
		assertEquals(WhatNowOut.State.PROCESSED, rootWhatNow.getState());
		assertEquals(root, rootWhatNow.getCurrent());
		
		assertEquals("Root flush.", results.get(10));
		
		assertFalse(rootWhatNow.after());
		
		assertEquals(11, results.size());
	}
	
	class FlushingOut implements DataOut {
		
		int flushed;
		
		final String name;
		
		FlushingOut(String name) {
			this.name = name;
		}
		
		@Override
		public boolean flush() throws DataException {
			++flushed;
			logger.info("Flushed " + name);
			return true;
		}
	}
	
	class SomeNode 
	extends MockDataNode<DataIn, DataIn, FlushingOut, FlushingOut> {
		
		final SomeNode[] children;
		
		FlushingOut out;
		
		final String name;
		
		SomeNode(String name, SomeNode...children) {
			this.name = name;
			this.children = children;
		}
		
		@Override
		public WhereNextOut<FlushingOut> out(
				FlushingOut outgoing) throws DataException {
			logger.info("Out " + name);
			
			if (children.length > 0) {
				if (out == null) {
					out = new FlushingOut(name);
				}
				return new WhereNextOut<FlushingOut>(children, out);
			}
			else {
				return new WhereNextOut<FlushingOut>();
			}
		}
		
		@Override
		public void flush(FlushingOut data, FlushingOut childData)
				throws DataException {
			childData.flush();
		}
		
		@Override
		public void complete(FlushingOut out) throws DataException {
		}
	}
	
	class DriverNode extends SomeNode implements DataDriver {
		public DriverNode(String name, SomeNode... children) {
			super(name, children);
		}
	}
	
	public void testFlushing() throws DataException {
		
		SomeNode root = new DriverNode("1",
				new SomeNode("1.1", new SomeNode("1.1.1")), 
				new SomeNode("1.2"));
		
		FlushingOut flushing = new FlushingOut("0");
		
		DataWriter<FlushingOut> test = 
			new DataWriter<FlushingOut>(root, flushing);
		
		test.write(new Object());
		
		assertNull(root.children[0].children[0].out);
		assertEquals(1, root.children[0].out.flushed);
		assertNull(root.children[1].out);
		assertEquals(1, root.out.flushed);
		assertEquals(0, flushing.flushed);
		
		test.write(new Object());
		
		assertNull(root.children[0].children[0].out);
		assertEquals(2, root.children[0].out.flushed);
		assertNull(root.children[1].out);
		assertEquals(2, root.out.flushed);
		assertEquals(0, flushing.flushed);
		
		test.complete();
		
		assertNull(root.children[0].children[0].out);
		assertEquals(2, root.children[0].out.flushed);
		assertNull(root.children[1].out);
		assertEquals(2, root.out.flushed);
		assertEquals(0, flushing.flushed);
	}
}
