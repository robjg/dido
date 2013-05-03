package org.oddjob.dido;

/**
 * Something that is a point onto what is to be read or written and it's
 * value.
 * <p>
 * Note that most implementations are likely to be bean with a <code>
 * getValue</code> and a <code>setValue</code> method, but because of 
 * <a href="http://bugs.sun.com/view_bug.do?bug-id=6528714">this bug</a> 
 * the property methods can not be declared generic.</p>
 * 
 * @author rob
 *
 * @param <T>
 */
public interface ValueNode<T> {

	/**
	 * Get the value just read or to be written.
	 * 
	 * @return A value. May be null.
	 */
//	public T getValue();
	public T value();
	
	/**
	 * Set the value to be written. Normally not relevant during
	 * reading.
	 * 
	 * @param value
	 */
//	public void setValue(T value);
	public void value(T value);
	
	public Class<T> getType();
}
