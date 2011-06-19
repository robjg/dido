package org.oddjob.dido.match;

/**
 * The result of a {@link Comparer} is a comparison between two things.
 * 
 * @author Rob
 *
 */
public interface Comparison {

	/**
	 * Is the result of the comparison equal.
	 * 
	 * @return true/false.
	 */
	public boolean isEqual();

	/**
	 * Provide a brief summary of the comparison.
	 * <p>
	 * If the comparison is equal then this should be the text representation
	 * of either of the original values. If the comparison is not equal then
	 * this should be a short description of the difference, 
	 * e.g. 'Fred <> Jane'.
	 * <p>
	 * As a rule of thumb summary should be suitable 
	 * for displaying in the column of a report or the cell of a
	 * spreadsheet.
	 * 
	 * @return A short text description of the comparison.
	 */
	public String getSummaryText();
}
