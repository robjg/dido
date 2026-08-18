package dido.how.util;

import java.util.Iterator;

/**
 * Iterates one ahead of an actual iterator. Used to derive a Schema from
 * data when unknown.
 *
 * @param <E> The element type.
 */
public class OneAheadIterator<E> implements Iterator<E> {

    private final Iterator<? extends E> original;

    private E current;

    public OneAheadIterator(Iterator<? extends E> original, E first) {
        this.original = original;
        this.current = first;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public E next() {
        try {
            return current;
        } finally {
            if (original.hasNext()) {
                current = original.next();
            } else {
                current = null;
            }
        }
    }
}
