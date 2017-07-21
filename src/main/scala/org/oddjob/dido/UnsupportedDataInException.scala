package org.oddjob.dido

/**
 * Something has gone wrong with reading or writing data.
 * 
 * @author rob
 *
 */
class UnsupportedDataInException(provider: Class[_ <: DataIn], required: Class[_ <: DataIn])
        extends DataException(provider.getName() + " cannot provide " + required.getName()) {

	private val serialVersionUID = 2010072700L

}
