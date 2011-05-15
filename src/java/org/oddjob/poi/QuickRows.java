package org.oddjob.poi;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.BeanView;
import org.oddjob.arooa.reflect.FallbackBeanView;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.DataDriver;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.SupportsChildren;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.io.ClassMorphic;

public class QuickRows 
implements DataNode<SheetIn, SheetIn, SheetOut, SheetOut>,
		ArooaSessionAware,
		ClassMorphic,
		SupportsChildren,
		DataDriver {
	
	private final static Set<Class<?>> NUMERIC_PRIMATIVES = 
		new HashSet<Class<?>>();
	
	static {
		
		NUMERIC_PRIMATIVES.add(byte.class);
		NUMERIC_PRIMATIVES.add(short.class);
		NUMERIC_PRIMATIVES.add(int.class);
		NUMERIC_PRIMATIVES.add(long.class);
		NUMERIC_PRIMATIVES.add(float.class);
		NUMERIC_PRIMATIVES.add(double.class);
	}
	
	private final DataRows dataRows = new DataRows();

	private ArooaSession session;
	
	private boolean initialised;
	
	private BeanView beanView;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Override
	public void beFor(ArooaClass arooaClass) {
		
		if (initialised) {
			throw new IllegalStateException(
					"QuickSheet Already initialised.");
		}
		
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		BeanOverview overview = 
			arooaClass.getBeanOverview(accessor);
		
		dataRows.setWithHeadings(true);
		
		BeanView beanView = this.beanView;
		if (beanView == null) {
			beanView = new FallbackBeanView(accessor, arooaClass);
		}
		
		int i = 0;
		for (String property : beanView.getProperties()) {
			
			if ("class".equals(property)) {
				continue;
			}
			
			Class<?> propertyType = overview.getPropertyType(property);
			
			DataCell<?> cell;
			
			if (NUMERIC_PRIMATIVES.contains(propertyType) || 
					Number.class.isAssignableFrom(propertyType)) {
				cell = new NumericCell();
			}
			else if (Boolean.class == propertyType || 
					boolean.class == propertyType) {
				cell = new BooleanCell();
			}
			else if (Date.class.isAssignableFrom(propertyType)) {
				cell = new DateCell();
			}
			else {
				cell = new TextCell();
			}
			
			cell.setName(property);
			cell.setTitle(beanView.titleFor(property));
			
			dataRows.setIs(i++, cell);
		}
		
		initialised = true;
	}
		
	@Override
	public WhereNextIn<SheetIn> in(
			SheetIn din) throws DataException {
		return dataRows.in(din);
	}
	
	@Override
	public void complete(SheetIn data) throws DataException {
		dataRows.complete(data);
		clearDataRowsChildren();
		initialised = false;
	}
	
	private void clearDataRowsChildren() {
		int childCount = dataRows.childrenToArray().length;
		while (childCount-- > 0) {
			dataRows.setIs(0, null);
		}
	}
	
	@Override
	public WhereNextOut<SheetOut> out(
			SheetOut dout) throws DataException {

		return dataRows.out(dout);
	}

	@Override
	public void flush(SheetOut data, SheetOut childData) throws DataException {
		dataRows.flush(data, childData);
	}
	
	@Override
	public void complete(SheetOut dout) throws DataException {
		dataRows.complete(dout);
		clearDataRowsChildren();
		initialised = false;
	}
	
	@Override
	public String getName() {
		return dataRows.getName();
	}
	
	public void setName(String name) {
		this.dataRows.setName(name);
	}
	
	@Override
	public DataNode<?, ?, ?, ?>[] childrenToArray() {
		return dataRows.childrenToArray();
	}
		
	@Override
	public String toString() {
		String name = getName();
		return getClass().getSimpleName() + 
			(name == null ? "" : " " + name);
	}

	public BeanView getBeanView() {
		return beanView;
	}

	public void setBeanView(BeanView beanView) {
		this.beanView = beanView;
	}

	public boolean isAutoFilter() {
		return dataRows.isAutoFilter();
	}

	public void setAutoFilter(boolean autoFilter) {
		this.dataRows.setAutoFilter(autoFilter);
	}

	public boolean isAutoWidth() {
		return dataRows.isAutoWidth();
	}

	public void setAutoWidth(boolean autoWidth) {
		this.dataRows.setAutoWidth(autoWidth);
	}
	
}
