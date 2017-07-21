package org.oddjob.dido;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.layout.BindingHelper;
import org.oddjob.dido.layout.LayoutDirectoryFactory;
import org.oddjob.dido.layout.LayoutsByName;

import scala.collection.JavaConversions._

import scala.collection.JavaConverters._

/**
 *
 * @author rob
 *
 */
class DataReadJob
extends Callable[Int] with ArooaSessionAware with BeanDirectoryOwner  {

	private val delegate = new DataReadJobScala

  private var beans: java.util.Collection[Any] = _

	@ArooaHidden
	override def setArooaSession(session: ArooaSession): Unit = {
    delegate.setArooaSession(session)
	}

  @throws(classOf[DataException])
	override def call(): Int = return delegate.call()
	
	override def provideBeanDirectory() = delegate.provideBeanDirectory()
	
	def setBeans(beans: java.util.Collection[Any]) = {
    this.beans = beans
    delegate.setBeans(b => beans.add(b))
  }

  def getBeans(): java.util.Collection[Any] = {
    this.beans
  }


	def getLayout() = delegate.getLayout()

	def setLayout(definition: Layout) = delegate.setLayout(definition)

	def getData() = delegate.getData()

	def setData(input: DataIn) = delegate.setData(input)
	
	def setBindings(name: String, binding: Binding) = delegate.setBindings(name, binding)

	def getBindings(name: String ) = delegate.getBindings(name)

	def getName() = delegate.getName()

	def setName(name: String) = delegate.setName(name)

	def getCount() = delegate.getCount()

	override def toString: String = {
		if (delegate.getName() == null)
			getClass().getSimpleName();
		  else delegate.getName();
	}	
}
