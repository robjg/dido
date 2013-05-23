package org.oddjob.dido;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ClassResolverDescriptorFactory;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.types.ValueFactory;

/**
 * @oddjob.description Provide a data guide for a {@link DataWriteJob} or 
 * {@link DataReadJob}.
 * 
 * @author rob
 *
 */
public class DataPlanType 
implements ValueFactory<DataPlan>, 
		ArooaSessionAware {

	private ArooaSession session;
	
	/**
	 * The resource name to read DIDO descriptors.
	 */
	public static final String DIDO_DESCRIPTOR_RESOURCE = 
		"META-INF/dido-arooa.xml";

    /**
     * @oddjob.property
     * @oddjob.description The configuration that will be read to
     * create the guide.
     * @oddjob.required Yes.
     */	
	private ArooaConfiguration configuration;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Override
	public DataPlan toValue() throws ArooaConversionException {
		if (configuration == null) {
			throw new NullPointerException("No Configuration.");
		}
		
		ClassResolverDescriptorFactory descriptorFactory = 
			new ClassResolverDescriptorFactory(DIDO_DESCRIPTOR_RESOURCE,
					session.getArooaDescriptor().getClassResolver());
		
		ArooaDescriptor descriptor = 
			descriptorFactory.createDescriptor(null);
		
		StandardFragmentParser parser = new StandardFragmentParser(
				descriptor);
		parser.setArooaType(ArooaType.COMPONENT);
		
		ConfigurationHandle handle = null;
		try {
			handle = parser.parse(configuration);
		} 
		catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}
		
		return new DataPlan(
				handle.getDocumentContext().getSession(), 
				(Layout) parser.getRoot());
	}
	
	public void setConfiguration(ArooaConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public ArooaConfiguration getConfiguration() {
		return configuration;
	}
}
