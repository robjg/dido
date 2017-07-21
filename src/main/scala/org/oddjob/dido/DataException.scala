package org.oddjob.dido

/**
 * Something has gone wrong with reading or writing data.
 * 
 * @author rob
 *
 */
class DataException(message: String, cause: Throwable)
		extends Exception(message, cause) {
	private val serialVersionUID: Long = 2010072700L

	def this(message: String) = {
		this(message, null)
	}

	def this(cause: Throwable) {
		this(null, cause)
	}

}
