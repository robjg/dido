package dido.objects.stratagy;

import java.lang.reflect.Type;

/**
 * Something that sets values during object construction.
 *
 * @see ConstructionStrategy
 * @see ObjectConstructor
 */
public interface ValueSetter {

    String getName();

    Type getType();

    void setValue(ObjectConstructor<?> target, Object value);

}
