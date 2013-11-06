package org.oddjob.dido.text;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.oddjob.dido.DataException;
import org.oddjob.dido.Layout;


/**
 * @oddjob.description Perform a substitution on the value of the field when
 * reading and substitute the text when writing. 
 * 
 * @author rob
 *
 */
public class SubstitutionLayout 
extends AbstractFieldLayout<String> 
implements ArooaSessionAware {

	private static final Logger logger = Logger.getLogger(SubstitutionLayout.class);

	private String substitution;
	
	private ArooaSession session;
	
	@ArooaHidden
	@Override
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	protected String convertIn(String value) 
	throws DataException {
		ExpressionParser expressionParser = 
				session.getTools().getExpressionParser();

		String result = null;
		try {
			ParsedExpression parsed = expressionParser.parse(value);
			result = parsed.evaluate(session, String.class);		
		}
		catch (ArooaConversionException e) {
			throw new DataException("Failed substituting value " + value, e);
		}
		
		logger.trace("Replaced [" + value +
				"], with [" + result + "]");
		
		return result;
	}
	
	@Override
	protected String convertOut(String value) {
		return substitution;
	}
	
	public String getSubsitition() {
		return substitution;
	}

	public void setSubstitution(String substitution) {
		this.substitution = substitution;
	}
}
