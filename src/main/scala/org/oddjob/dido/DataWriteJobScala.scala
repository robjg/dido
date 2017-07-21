package org.oddjob.dido

import java.util.{HashMap, Map}
import java.util.concurrent.Callable

import org.apache.log4j.Logger
import org.oddjob.arooa.{ArooaSession, ArooaTools}
import org.oddjob.arooa.deploy.annotations.ArooaHidden
import org.oddjob.arooa.life.ArooaSessionAware
import org.oddjob.arooa.registry.{BeanDirectory, BeanDirectoryOwner}
import org.oddjob.dido.bio.Binding
import org.oddjob.dido.layout.{BindingHelper, LayoutDirectoryFactory, LayoutsByName}

import scala.collection.JavaConversions._

/**
 * @oddjob.description A Job that can write data out to a file or 
 * other output.  
 * 
 * @oddjob.exapmle
 * 
 * Writing a delimited file.
 * 
 * {@oddjob.xml.resource org/oddjob/dido/WriteJobExample.xml}
 * 
 * The plan is 
 * 
 * {@oddjob.xml.resource org/oddjob/dido/DelimitedExamplePlan.xml}
 * 
 * @author rob
 *
 */
class DataWriteJobScala
extends Callable[Int] with ArooaSessionAware with BeanDirectoryOwner {
	
	private val logger: Logger = Logger.getLogger(getClass)
	
    /**
     * @oddjob.property
     * @oddjob.description The job's name.
     * @oddjob.required No.
     */	
	private var name: String = _
	
    /**
     * @oddjob.property
     * @oddjob.description The layout of the data. This is the
     * root node of a hierarchy of layout nodes.
     * @oddjob.required Yes.
     */	
	private var layout: Layout = _
	
    /**
     * @oddjob.property
     * @oddjob.description The beans to write out.
     * @oddjob.required Yes.
     */	
	private var beans: Iterable[Any] = _
	
    /**
     * @oddjob.property
     * @oddjob.description The Data Out to write the data to.
     * @oddjob.required Yes.
     */	
	private var data: DataOut = _
	
    /**
     * @oddjob.property
     * @oddjob.description Bindings between beans and data out.
     * @oddjob.required Yes.
     */	
	private val bindings: Map[String, Binding] = new HashMap()
		
	/**
     * @oddjob.property
     * @oddjob.description The number of beans written.
     * @oddjob.required Read Only.
	 */
	private var count: Int = _
	
	/** Creates the bean directory from the layouts. */
	private var layoutDirectoryFactory: LayoutDirectoryFactory = _
	
	/** Set when running from the layout names. */
	private var beanDirectory: BeanDirectory = _
	
	@ArooaHidden
	override def setArooaSession(session: ArooaSession) = {
		val tools: ArooaTools = session.getTools();
		this.layoutDirectoryFactory = new LayoutDirectoryFactory(
				tools.getPropertyAccessor(), tools.getArooaConverter());
	}

  @throws(classOf[DataException])
	override def call(): Int = {
		
		count = 0;
		
		if (layout == null) {
			throw new NullPointerException("No Layout provided.");
		}
		
		if (data == null) {
			throw new NullPointerException("No output provided");
		}
		
		if (beans == null) {
			throw new NullPointerException("No beans provided");
		}

		logger.info("Starting to write data using [" + layout + "]");
				
		layout.reset();
		
		val layoutsByName: LayoutsByName = new LayoutsByName(layout);
		
		val bindingHelper: BindingHelper = new BindingHelper();
		
		for (entry: Map.Entry[String, Binding] <- bindings.entrySet()) {
			
			val layout: Layout  = layoutsByName.getLayout(entry.getKey());

			if (layout == null) {
				logger.warn("No Layout to bind to named " + name);
			}
			else {
				bindingHelper.bind(layout, entry.getValue());
			}
		}
		
		if (layoutDirectoryFactory == null) {
			logger.debug("No session set so no bean directory available.");
		}
		else {
			beanDirectory = layoutDirectoryFactory.createFrom(
					layoutsByName.getAll());
		}
		
		val writer: DataWriter = layout.writerFor(data);
		
		try {
			for (bean <- beans) {
				
				if (Thread.interrupted()) {
					logger.info("Interrupted.");
					return 1;
				}
				
				writer.write(bean);
				
				count += 1;
			}
		}
		finally {
			logger.info("Wrote " + count + " beans.");
			
			try {
				writer.close();			
			}
			finally {
				bindingHelper.freeAll();
			}
		}
		
		return 0;
	}	
	
	override def provideBeanDirectory(): BeanDirectory = {
		return beanDirectory;
	}
	
	def setBeans(beans: Iterable[Any]): Unit = {
		this.beans = beans;
	}

	def getLayout(): Layout = {
		layout;
	}

	def setLayout(definition: Layout ): Unit = {
		this.layout = definition;
	}


	def getData(): DataOut = {
		return data;
	}


	def setData(output: DataOut): Unit = {
		this.data = output;
	}
	
	def setBindings(name: String, binding: Binding): Unit = {
		if (binding == null) {
			bindings.remove(name);
		}
		else {
			bindings.put(name, binding);
		}
	}
	
	def getBindings(name: String): Binding = {
		return bindings.get(name);
	}

	def getName(): String = {
		return name;
	}

	def setName(name: String): Unit = {
		this.name = name;
	}
	
	override def toString(): String = {
		if (name != null) {
			return name;
		}
		else {
			return getClass().getSimpleName();
		}
	}

	def getCount(): Int = {
		return count;
	}
}
