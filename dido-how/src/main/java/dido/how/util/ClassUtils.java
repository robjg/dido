package dido.how.util;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Various utility methods relating to class.
 * 
 * @author Rob Gordon.
 */
public class ClassUtils {

	/**
	 * Primitive type class names to types.
	 */
	private static final Map<String, Class<?>> primitiveNameToTypeMap =
			new HashMap<>(9);
	

	static {
		Class<?>[] primitives = {
				void.class,
				boolean.class, byte.class, char.class, double.class,
				float.class, int.class, long.class, short.class };

		for (Class<?> primitive : primitives) {
			primitiveNameToTypeMap.put(primitive.getName(),
					primitive);
		}		
		
	}

	/**
	 * Same as {@link Class#forName} except copes with primitives.
	 *
	 * @param className The name of the class
	 * @param loader The Class Loader.
	 * @return The Class.
	 *
	 * @throws ClassNotFoundException If it can't be found.
	 */
	public static Class<?> classFor(String className, ClassLoader loader) 
	throws ClassNotFoundException {

		Class<?> theClass = primitiveNameToTypeMap.get(Objects.requireNonNull(className, "No class name."));
		if (theClass == null) {
			return Class.forName(className, true, loader);
		}
		else {
			return theClass;
		}
	}
}
