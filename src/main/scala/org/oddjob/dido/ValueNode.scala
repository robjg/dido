package org.oddjob.dido

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
 * @tparam T Type of the value
 */
trait ValueNode[T] {

	/**
	 * Get the value just read or to be written.
	 * 
	 * @return A value. May be null.
	 */
	def value: T
	
	/**
	 * Set the value just read or to be written. 
	 * 
	 * @param value The value. Set with null to clear the last value.
	 */
	def value(value: T): Unit
	
	/**
	 * Get the type. Note that this can't be Class&lt;T&gt; because of Java's
	 * inability to resolve an expression such as 
	 * <code>List&lt;Integer&gt;.class</code>.
	 * 
	 * @return The type that must be of Class&lt;T&gt;
	 */
	def getType: Class[_]
}
