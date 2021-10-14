package org.oddjob.dido.tabular;

import org.oddjob.dido.field.FieldIn;

/**
 * Representation of a column that data can be read from. 
 * 
 * @see TabularDataIn
 * 
 * @author rob
 *
 * @param <T> The type of data.
 */
public interface ColumnIn<T> extends FieldIn<T>, ColumnData {

}
