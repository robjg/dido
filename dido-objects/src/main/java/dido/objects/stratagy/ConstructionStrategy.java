package dido.objects.stratagy;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Something that provides the means of constructing objects by setting values.
 *
 * @param <T> The type of object.
 */
public interface ConstructionStrategy<T> {

    Type getType();

    Supplier<ObjectConstructor<T>> getConstructorSupplier();

    Collection<ValueSetter> getSetters();

    ValueSetter getSetter(String name);

}
