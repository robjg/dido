package dido.objects.stratagy;

/**
 * Allows values to be set during object construction.
 *
 * @param <T> The type of object that is being constructed.
 *
 * @see ValueSetter
 */
public interface ObjectConstructor<T> {

    T actualize();
}
