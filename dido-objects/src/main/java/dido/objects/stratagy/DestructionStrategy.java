package dido.objects.stratagy;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Strategy for Destructing an object using {@link ValueGetter}s
 *
 * @see ConstructionStrategy
 */
public interface DestructionStrategy {

    Type getType();

    Collection<ValueGetter> getGetters();

    ValueGetter getGetter(String name);
}
