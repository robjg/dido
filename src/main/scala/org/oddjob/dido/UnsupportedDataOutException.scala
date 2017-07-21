package org.oddjob.dido

/**
 * Thrown when a [[DataOut]] doesn't support the required type.
 * 
 * @author rob
 *
 */
class UnsupportedDataOutException(provider: Class[_ <: DataOut], required: Class[_ <: DataOut])
	extends DataException(provider.getName() + " cannot provide " + required.getName()) {

	private val serialVersionUID = 2010072700L

}