package org.oddjob.dido.bio;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.io.ClassMorphic;
import org.oddjob.dido.io.DataLinkOut;
import org.oddjob.dido.io.LinkOutEvent;
import org.oddjob.dido.io.LinkableOut;
import org.oddjob.dido.io.Nodes;

public class LateBindingBean implements BindingOut, ArooaSessionAware {
	private static final Logger logger = Logger.getLogger(
			LateBindingBean.class);
	
	private PropertyAccessor accessor;
	
	private String node;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.accessor = session.getTools().getPropertyAccessor(
				).accessorWithConversions(
						session.getTools().getArooaConverter());
	}
	
	@Override
	public void bindTo(DataNode<?, ?, ?, ?> root, LinkableOut linkable) {
		
		if (node == null) {
			throw new NullPointerException("No Node.");
		}
		
		Nodes nodes = new Nodes(root);
	
		DataNode<?, ?, ?, ?> bindTo = nodes.getNode(node);
		
		if (bindTo == null) {
			throw new IllegalArgumentException("Failed to find " + node);
		}
		
		logger.debug("Binding to " + node);
		
		linkable.setLinkOut(bindTo, new LateBindingLink());
	}
	
	class LateBindingLink implements DataLinkOut {
		
		private boolean initialised;
		
		@Override
		public boolean dataOut(LinkOutEvent event, Object bean) {
			if (!initialised) {				
				
				DataNode<?, ?, ?, ?> linkNode = 
					event.getNode();
				
				if (linkNode instanceof ClassMorphic) {
					((ClassMorphic) linkNode).beFor(
							accessor.getClassName(bean));
				}
				
				LinkableOut linkable = event.getSource();
				
				PropertiesToNodes propertyBinding = 
					new PropertiesToNodes(accessor.getClassName(bean), 
							accessor);
			
				propertyBinding.bindTo(linkNode, linkable);
				
				initialised = true;
			}
			
			return true;
		}
		
		@Override
		public void lastOut(LinkOutEvent event) {
		}
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}
}
