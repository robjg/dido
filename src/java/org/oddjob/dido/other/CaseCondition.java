package org.oddjob.dido.other;

/**
 * Something that can provide a condition for a {@link Case} data node.
 * 
 * @author rob
 *
 * @param <TYPE>
 */
public interface CaseCondition<TYPE> {
	
	/**
	 * Evaluate this condition against the given value.
	 * 
	 * @param against The value to evaluate against.
	 * 
	 * @return true/false depending on the result of the evaluation.
	 */
	public boolean evaluate(TYPE against);
	
	/**
	 * Evaluate for output. Generally used in conjunction with {@link Selected}
	 * to provide the value that is used to write to the descriminator node
	 * of a {@link Case} node.
	 *  
	 * @return The value for the descriminator of null if this CaseCondition
	 * wasn't selected.
	 */
	public TYPE evaluateOut();
	
	/**
	 * The class type of the condition.
	 * 
	 * @return
	 */
	public Class<TYPE> getType();
}
