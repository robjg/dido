package org.oddjob.dido;

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

/**
 * #oddjob.description A Job that can write data out to a file or
 * other output.  
 * 
 * #oddjob.example
 * 
 * Writing a delimited file.
 * 
 * {#oddjob.xml.resource org/oddjob/dido/WriteJobExample.xml}
 * 
 * The plan is 
 * 
 * {#oddjob.xml.resource org/oddjob/dido/DelimitedExamplePlan.xml}
 * 
 * @author rob
 *
 */
class DataWriteJob
extends Callable[Int] with ArooaSessionAware with BeanDirectoryOwner {

	private val delegate = new DataWriteJobScala

	@ArooaHidden
	override def setArooaSession(session: ArooaSession) = delegate.setArooaSession(session)

  @throws(classOf[DataException])
	override def call(): Int = delegate.call()
	
	override def provideBeanDirectory(): BeanDirectory = delegate.provideBeanDirectory()
	
	def setBeans(beans: java.lang.Iterable[Any]): Unit = delegate.setBeans(beans)

	def getLayout(): Layout = delegate.getLayout()

	def setLayout(definition: Layout ): Unit = delegate.setLayout(definition)

	def getData(): DataOut = delegate.getData()

	def setData(output: DataOut): Unit = delegate.setData(output)
	
	def setBindings(name: String, binding: Binding): Unit = delegate.setBindings(name, binding)
	
	def getBindings(name: String): Binding = delegate.getBindings(name)

	def getName(): String = delegate.getName()

	def setName(name: String): Unit = delegate.setName(name)

  def getCount(): Int = delegate.getCount()

	override def toString(): String = {
    Option(delegate.getName()).getOrElse(getClass().getSimpleName())
	}

}
