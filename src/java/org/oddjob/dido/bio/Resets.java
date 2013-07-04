package org.oddjob.dido.bio;

import java.util.ArrayList;
import java.util.List;

public class Resets {

	private final List<Runnable> resets = new ArrayList<Runnable>();
	
	public void add(Runnable reset) {
		resets.add(reset);
	}
	
	public void reset() {
		for (Runnable reset : resets) {
			reset.run();
		}
		resets.clear();
	}
}
