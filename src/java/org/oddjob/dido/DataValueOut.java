package org.oddjob.dido;

public interface DataValueOut {

	public <T> T toValue(Class<T> type);
}
