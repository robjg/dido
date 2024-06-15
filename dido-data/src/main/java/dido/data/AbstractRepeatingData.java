package dido.data;

import java.util.Iterator;
import java.util.Objects;

abstract public class AbstractRepeatingData implements RepeatingData {

    private volatile int hashCode;

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (! (o instanceof RepeatingData)) {
            return false;
        }

        RepeatingData other = (RepeatingData) o;

        if (other.size() != this.size()) {
            return false;
        }

        Iterator<? extends IndexedData> it1 = iterator();
        Iterator<? extends IndexedData> it2 = other.iterator();

        boolean it1HasNext;
        boolean it2HasNext;
        while ((it1HasNext = it1.hasNext()) & (it2HasNext = it2.hasNext())) {
            if (!(Objects.equals(it1.next(), it2.next())))
                return false;
        }

        return !it1HasNext && !it2HasNext;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hashCode = 1;
            for (Object e : this)
                hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
            this.hashCode = hashCode;
        }
        return this.hashCode;
    }

    @Override
    public String toString() {

        Iterator<?> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder(size() * 64);

        sb.append('[');
        for (;;) {
            Object e = it.next();
            sb.append(e == this ? "(this repeating data)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

}
