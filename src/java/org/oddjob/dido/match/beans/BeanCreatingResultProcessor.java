package org.oddjob.dido.match.beans;

import java.util.Collection;

import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.match.matchables.Matchable;
import org.oddjob.dido.match.matchables.MatchableComparison;
import org.oddjob.dido.match.matchables.MatchableGroup;
import org.oddjob.dido.match.matchables.MatchableMatchProcessor;

/**
 * Creates result beans for matches.
 * 
 * @author rob
 *
 */
public class BeanCreatingResultProcessor implements MatchableMatchProcessor {

	private final MatchResultBeanFactory factory;
	
	private final Collection<Object> out;
	
	public BeanCreatingResultProcessor(
			PropertyAccessor accessor, 
			String xPropertyPrefix,
			String yPropertyPrefix,			
			Collection<Object> collection) {
		
		factory = new SimpleResultBeanFactory(accessor, 
				xPropertyPrefix, yPropertyPrefix);
		
		this.out = collection;
	}
		
	@Override
	public void matched(Matchable x, Matchable y,
			MatchableComparison comparison) {
		out.add(factory.createResult(x, y, comparison));
	}
	
	@Override
	public void xMissing(MatchableGroup ys) {
		for (Matchable y : ys.getGroup()) {			
			out.add(factory.createResult(null, y, null));
		}
	}
	
	@Override
	public void yMissing(MatchableGroup xs) {
		for (Matchable x : xs.getGroup()) {
			out.add(factory.createResult(x, null, null));
		}
	}	
}
