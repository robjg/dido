package org.oddjob.dido.io;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.dido.DataDriver;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.MockBoundedDataNode;
import org.oddjob.dido.MockDataIn;
import org.oddjob.dido.MockDataNode;
import org.oddjob.dido.WhereNext;
import org.oddjob.dido.WhereNextIn;


public class WhatNowInTest extends TestCase {

	
	class Root extends MockBoundedDataNode<DataIn, DataIn, DataOut, DataOut> {
		Middle child = new Middle();
		
		@Override
		public WhereNext<DataNode<DataIn, ?, ?, ?>, DataIn> in(
				DataIn data) throws DataException {
			Middle[] children = { child };
			
			return new WhereNextIn<DataIn>(children, 
					new MockDataIn());
		}
	}
	
	class Middle extends MockDataNode<DataIn, DataIn, DataOut, DataOut> 
	implements DataDriver {
		
		int count;
		
		Leaf[] children = { new Leaf("One"), new Leaf("Two") };
				
		@Override
		public WhereNext<DataNode<DataIn, ?, ?, ?>, DataIn> in(
				DataIn data) throws DataException {
			
			if (count == 0) {
				results.add("Middle starting.");
				
				for (Leaf leaf : children) {
					leaf.begin(new MockDataIn());
				}
			}
			
			if (++count == 3) {
				return null;
			}
			else {
				return new WhereNextIn<DataIn>(children,
						new MockDataIn());
			}
		}
		
		@Override
		public void complete(DataIn data) throws DataException {
			for (Leaf leaf : children) {
				leaf.end(new MockDataIn());
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
		public void begin(DataIn data) {
			results.add("Leaf " + name + " begin.");
		}
		
		@Override
		public void end(DataIn data) {
			results.add("Leaf " + name + " end.");
		}
		
		@Override
		public WhereNext<DataNode<DataIn, ?, ?, ?>, DataIn> in(
				DataIn data) throws DataException {
			return new WhereNextIn<DataIn>();
		}
		
		@Override
		public void complete(DataIn in) throws DataException {
		}
		
		@Override
		public String toString() {
			return "Leaf " + name;
		}
	}
	
	List<String> results = new ArrayList<String>();
	
	public void testFullCycle() throws DataException {
		
		Root root = new Root();
		
		DataIn rootData = new MockDataIn();
		
		WhatNowIn rootNew = new NewWhatNowIn(root, rootData);
		
		assertEquals(WhatNowIn.State.NEW, rootNew.getState());
		assertEquals(root, rootNew.getCurrent());
		
		assertEquals(0, results.size());
		
		WhatNowIn rootChildren = rootNew.currentIn();
		
		assertEquals(WhatNowIn.State.CHILDREN, rootChildren.getState());
		assertEquals(root, rootChildren.getCurrent());
		
		List<WhatNowIn> childWhats = rootChildren.nextChild();
		
		assertEquals(2, childWhats.size());		
		assertEquals(rootChildren, childWhats.get(0));
		
		WhatNowIn middle1New = childWhats.get(1);

		assertEquals(WhatNowIn.State.NEW, middle1New.getState());
		assertEquals(NewWhatNowIn.class, middle1New.getClass());
		assertEquals("Middle", middle1New.getCurrent().toString());
		
		WhatNowIn middle1Children = middle1New.currentIn();
		
		assertEquals(WhatNowIn.State.CHILDREN, middle1Children.getState());
		assertEquals("Middle", middle1Children.getCurrent().toString());
		
		assertEquals(3, results.size());
		assertEquals("Middle starting.", results.get(0));
		assertEquals("Leaf One begin.", results.get(1));
		assertEquals("Leaf Two begin.", results.get(2));
		
		childWhats = middle1Children.nextChild();
		
		assertEquals(2, childWhats.size());
		assertEquals(middle1Children, childWhats.get(0));
		
		WhatNowIn leaf1OneNew = childWhats.get(1);
		
		assertEquals(WhatNowIn.State.NEW, leaf1OneNew.getState());
		assertEquals(NewWhatNowIn.class, leaf1OneNew.getClass());
		assertEquals("Leaf One", leaf1OneNew.getCurrent().toString());
		
		WhatNowIn leafOneProcessed = leaf1OneNew.currentIn();
		
		assertEquals(WhatNowIn.State.PROCESSED, leafOneProcessed.getState());
		assertEquals("Leaf One", leafOneProcessed.getCurrent().toString());
		
		WhatNowIn leafOneAfter = leafOneProcessed.getAfter();
		
		assertNull(leafOneAfter);
		
		childWhats = middle1Children.nextChild();
		
		assertEquals(2, childWhats.size());
		WhatNowIn leaf1TwoNew = childWhats.get(1);
		
		assertEquals(WhatNowIn.State.NEW, leaf1TwoNew.getState());
		assertEquals(NewWhatNowIn.class, leaf1TwoNew.getClass());
		assertEquals("Leaf Two", leaf1TwoNew.getCurrent().toString());
		
		WhatNowIn leaf1TwoProcessed = leaf1TwoNew.currentIn();
		
		assertEquals(WhatNowIn.State.PROCESSED, leaf1TwoProcessed.getState());
		assertEquals("Leaf Two", leaf1TwoProcessed.getCurrent().toString());
		
		WhatNowIn leaf1TwoAfter = leaf1TwoProcessed.getAfter();
		
		assertNull(leaf1TwoAfter);
		
		childWhats = middle1Children.nextChild();
		
		assertEquals(1, childWhats.size());
		
		WhatNowIn middle1Processed = childWhats.get(0);
				
		assertEquals(WhatNowIn.State.PROCESSED, middle1Processed.getState());
		assertEquals("Middle", middle1Processed.getCurrent().toString());
		
		WhatNowIn middle2New = middle1Processed.getAfter();
		
		assertEquals(WhatNowIn.State.NEW, middle2New.getState());
		assertEquals(RepeatedWhatNowIn.class, middle2New.getClass());
		assertEquals("Middle", middle2New.getCurrent().toString());
		
		WhatNowIn middle2Children = middle2New.currentIn();
		
		assertEquals(3, results.size());
		
		assertEquals(WhatNowIn.State.CHILDREN, middle2Children.getState());
		assertEquals("Middle", middle2Children.getCurrent().toString());
		
		childWhats = middle2Children.nextChild();
		
		assertEquals(2, childWhats.size());
		assertEquals(middle2Children, childWhats.get(0));
		
		WhatNowIn leaf2OneNew = childWhats.get(1);
		
		assertEquals(WhatNowIn.State.NEW, leaf2OneNew.getState());
		assertEquals(NewWhatNowIn.class, leaf2OneNew.getClass());
		assertEquals("Leaf One", leaf2OneNew.getCurrent().toString());
		
		WhatNowIn leaf2OneProcessed = leaf2OneNew.currentIn();
		
		assertEquals(WhatNowIn.State.PROCESSED, leaf2OneProcessed.getState());
		assertEquals("Leaf One", leaf2OneProcessed.getCurrent().toString());
		
		WhatNowIn leaf2OneAfter = leafOneProcessed.getAfter();
		
		assertNull(leaf2OneAfter);
		
		childWhats = middle2Children.nextChild();
		
		assertEquals(2, childWhats.size());
		WhatNowIn leaf2TwoNew = childWhats.get(1);
		
		assertEquals(WhatNowIn.State.NEW, leaf2TwoNew.getState());
		assertEquals(NewWhatNowIn.class, leaf1OneNew.getClass());
		assertEquals("Leaf Two", leaf2TwoNew.getCurrent().toString());
		
		WhatNowIn leaf2TwoProcessed = leaf1TwoNew.currentIn();
		
		assertEquals(WhatNowIn.State.PROCESSED, leaf1TwoProcessed.getState());
		assertEquals("Leaf Two", leaf2TwoProcessed.getCurrent().toString());
		
		WhatNowIn leaf2TwoAfter = leaf2TwoProcessed.getAfter();
		
		assertNull(leaf2TwoAfter);
		
		childWhats = middle2Children.nextChild();
		
		assertEquals(1, childWhats.size());
		
		WhatNowIn middle2Processed = childWhats.get(0);
				
		assertEquals(WhatNowIn.State.PROCESSED, middle2Processed.getState());
		assertEquals("Middle", middle2Processed.getCurrent().toString());
		
		WhatNowIn middle3New = middle2Processed.getAfter();
		
		assertEquals(WhatNowIn.State.NEW, middle3New.getState());
		assertEquals(RepeatedWhatNowIn.class, middle3New.getClass());
		assertEquals("Middle", middle3New.getCurrent().toString());
		
		assertEquals(3, results.size());
		
		assertNull(middle2New.currentIn());
		
		assertEquals(6, results.size());
		assertEquals("Leaf One end.", results.get(3));
		assertEquals("Leaf Two end.", results.get(4));
		assertEquals("Middle complete.", results.get(5));
		
		childWhats = rootChildren.nextChild();
		
		assertEquals(1, childWhats.size());
		
		WhatNowIn rootProcessed = childWhats.get(0);
		
		assertEquals(WhatNowIn.State.PROCESSED, rootProcessed.getState());
		assertEquals(root, rootProcessed.getCurrent());
		
		assertNull(rootProcessed.getAfter());		
	}
}
