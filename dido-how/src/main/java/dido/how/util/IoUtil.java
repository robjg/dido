package dido.how.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public class IoUtil {

    public static Writer writerFromAppendable(Appendable appendable) {
        return new AppendableWriter(appendable);
    }

    /**
     * Adapts an {@link Appendable} so it can be passed anywhere a {@link Writer}
     * is used.
     * From GSON Streams.
     */
    private static final class AppendableWriter extends Writer {
        private final Appendable appendable;
        private final CurrentWrite currentWrite = new CurrentWrite();

        AppendableWriter(Appendable appendable) {
            this.appendable = appendable;
        }

        @Override public void write(char[] chars, int offset, int length) throws IOException {
            currentWrite.chars = chars;
            appendable.append(currentWrite, offset, offset + length);
        }

        @Override public void write(int i) throws IOException {
            appendable.append((char) i);
        }

        @Override public void flush() {}

        @Override public void close() throws IOException{
            if (appendable instanceof Closeable) {
                ((Closeable) appendable).close();
            }
        }

        /**
         * A mutable char sequence pointing at a single char[].
         */
        static class CurrentWrite implements CharSequence {
            char[] chars;
            public int length() {
                return chars.length;
            }
            public char charAt(int i) {
                return chars[i];
            }
            public CharSequence subSequence(int start, int end) {
                return new String(chars, start, end - start);
            }
        }
    }

    public static AutoCloseable closeableOf(Object maybeClosable) {

        if (maybeClosable instanceof AutoCloseable) {
            return () -> ((AutoCloseable) maybeClosable).close();
        }
        else {
            return () -> {};
        }

    }

}
