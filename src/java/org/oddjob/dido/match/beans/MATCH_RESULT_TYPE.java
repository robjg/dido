package org.oddjob.dido.match.beans;

/**
 * The type of the result. Used as a property on a match result bean.
 * 
 * @author rob
 *
 */
public enum MATCH_RESULT_TYPE {

	/** No X data was found matching the Y key. */
	X_MISSING,
	
	/** No Y data was found matching the X key. */
	Y_MISSING,
	
	/** Two things matched by key and their values were equal. */
	EQUAL,
	
	/** Two things matched by key but one or more of there values
	 * were not equal.
	 */
	NOT_EQUAL,
}