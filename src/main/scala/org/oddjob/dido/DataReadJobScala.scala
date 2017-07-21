package org.oddjob.dido

import java.util.concurrent.Callable

import org.apache.log4j.Logger
import org.oddjob.arooa.{ArooaSession, ArooaTools}
import org.oddjob.arooa.deploy.annotations.ArooaHidden
import org.oddjob.arooa.life.ArooaSessionAware
import org.oddjob.arooa.registry.{BeanDirectory, BeanDirectoryOwner}
import org.oddjob.dido.bio.Binding
import org.oddjob.dido.layout.{BindingHelper, LayoutDirectoryFactory, LayoutsByName}

/**
 * #oddjob.description A Job that can read data from a file or
 * some other input.  
 * 
 * #oddjob.example
 * 
 * Reading a delimited file.
 * 
 * {#oddjob.xml.resource org/oddjob/dido/ReadJobExample.xml}
 * 
 * The plan is 
 * 
 * {#oddjob.xml.resource org/oddjob/dido/DelimitedExamplePlan.xml}
 * 
 * @author rob
 *
 */
class DataReadJobScala
extends Callable[Int] with ArooaSessionAware with BeanDirectoryOwner  {

	private val logger = Logger.getLogger(getClass)
	
	/** 
	 * #oddjob.property
	 * #oddjob.description The name of this job.
	 * #oddjob.required No.
	 */
	private var name: String = _
	
    /**
     * #oddjob.property
     * #oddjob.description The layout of the data. This is the
     * root node of a structure of layout nodes.
     * #oddjob.required Yes.
     */	
	private var layout: Layout = _

    /**
     * #oddjob.property
     * #oddjob.description The beans read in.
     * #oddjob.required R/O.
     */	
	private var beans: (Any) => Unit = _
	
	
    /**
     * #oddjob.property
     * #oddjob.description A count of the number of beans read.
     * #oddjob.required R/O.
     */	
	private var count: Int = _
	
    /**
     * #oddjob.property
     * #oddjob.description The data to read.
     * #oddjob.required Yes.
     */	
	private var data: DataIn = _
	
    /**
     * #oddjob.property
     * #oddjob.description Bindings between beans and data in.
     * #oddjob.required No, but pointless if missing.
     */	
	private var bindings: Map[String, Binding]= Map()

	/** Creates the bean directory from the layouts. */
	private var layoutDirectoryFactory: LayoutDirectoryFactory = _
	
	/** Set when running from the layout names. */
	private var beanDirectory: BeanDirectory = _
	
	@ArooaHidden
	@Override
	def setArooaSession(session: ArooaSession): Unit = {
		val tools: ArooaTools = session.getTools()
		this.layoutDirectoryFactory = new LayoutDirectoryFactory(
      tools.getPropertyAccessor(), tools.getArooaConverter())
	}

  @throws(classOf[DataException])
	override def call(): Int = {
		
		count = 0
		
		if (layout == null) {
			throw new NullPointerException("No Layout provided.")
		}
		
		if (data == null) {
			throw new NullPointerException("No Input provided.")
		}
		
		if (beans == null) {
			logger.info("No destination for beans!")
		}
		
		layout.reset()
		
		val layoutsByName = new LayoutsByName(layout)
		
		val bindingHelper = new BindingHelper()
		
		for (entry: (String, Binding) <- bindings) {
			
			val layout = layoutsByName.getLayout(entry._1)
			if (layout == null) {
				logger.warn("No Layout to bind to named " + name)
			}
			else {
				bindingHelper.bind(layout, entry._2)
			}
		}
		
		if (layoutDirectoryFactory == null) {
			logger.debug("No session set so no bean directory available.");
		}
		else {
			beanDirectory = layoutDirectoryFactory.createFrom(
					layoutsByName.getAll())
		}
		
		logger.info("Starting to read data using [" + layout + "]")
		
		val reader = layout.readerFor(data)

		try {
			var result = -1

			while (result < 0) {
				
				if (Thread.interrupted()) {
					logger.info("Interrupted.")
					result = 1
				}
        else {
          val bean = reader.read()
          if (bean== null) {
            result = 0
          }
          else {
            if (beans != null) {
              beans(bean)
            }
            count += 1
          }
        }
			}

      result
		}
		finally {
			logger.info("Read " + count + " beans.")
			
			try {
				reader.close()
			}
			finally {
				bindingHelper.freeAll()
			}
		}
	}
	
	override def provideBeanDirectory(): BeanDirectory = beanDirectory

	def setBeans(beans: (Any) => Unit) {
		this.beans = beans;
	}

	def getLayout() = {
		layout;
	}

	def setLayout(definition: Layout) = {
		this.layout = definition;
	}

	def getData() = {
		data;
	}

	def setData(input: DataIn ) = {
		this.data = input;
	}
	
	def setBindings(name: String, binding: Binding) = {
		if (binding == null) {
			bindings = bindings - name
		}
		else {
			bindings = bindings + (name -> binding);
		}
	}
	
	def getBindings(name: String ): Binding = {
		bindings(name)
	}


	def getName() = {
		name;
	}


	def setName(name: String) = {
		this.name = name;
	}
	
	def getCount() = {
		count;
	}
	
	override def toString: String = {
		if (name == null) {
			getClass().getSimpleName();
		}
		else name;
	}	
}
