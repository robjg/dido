package org.oddjob.dido.match;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Iterables {

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Iterable<T> iterable, Class<?> type) {

		List<T> list = new ArrayList<T>();
		
		for (T t : iterable) {
			list.add(t);
		}
		
		return list.toArray((T[]) Array.newInstance(type, list.size()));
	}
}
