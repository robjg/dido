package dido.objects.stratagy;

import java.lang.reflect.Type;

/**
 * Able to extract a value from an Object.
 *
 * @see DestructionStrategy
 */
public interface ValueGetter {

    String getName();

    Type getType();

    Object getValue(Object target);
}
