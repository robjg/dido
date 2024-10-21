package dido.how;

import java.io.*;

public interface CloseableAppendable extends Appendable, Closeable {

    static CloseableAppendable fromAppendable(Appendable appendable) {
        if (appendable instanceof CloseableAppendable) {
            return ((CloseableAppendable) appendable);
        }

        return new CloseableAppendable() {
            @Override
            public void close() {
            }

            @Override
            public CloseableAppendable append(CharSequence csq) throws IOException {
                appendable.append(csq);
                return this;
            }

            @Override
            public CloseableAppendable append(CharSequence csq, int start, int end) throws IOException {
                appendable.append(csq, start, end);
                return this;
            }

            @Override
            public CloseableAppendable append(char c) throws IOException {
                appendable.append(c);
                return this;
            }
        };
    }

    static CloseableAppendable fromOutputStream(OutputStream outputStream) {
        return fromPrintStream(outputStream instanceof PrintStream ?
                ((PrintStream) outputStream) : new PrintStream(outputStream));
    }

    static CloseableAppendable fromPrintStream(PrintStream printStream) {
        return new CloseableAppendable() {
            @Override
            public void close() {
                printStream.close();
            }

            @Override
            public CloseableAppendable append(CharSequence csq) {
                printStream.append(csq);
                return this;
            }

            @Override
            public CloseableAppendable append(CharSequence csq, int start, int end) {
                printStream.append(csq, start, end);
                return this;
            }

            @Override
            public CloseableAppendable append(char c) {
                printStream.append(c);
                return this;
            }
        };
    }

    static CloseableAppendable fromWriter(Writer writer) {
        return new CloseableAppendable() {
            @Override
            public void close() throws IOException {
                writer.close();
            }

            @Override
            public CloseableAppendable append(CharSequence csq) throws IOException {
                writer.append(csq);
                return this;
            }

            @Override
            public CloseableAppendable append(CharSequence csq, int start, int end) throws IOException {
                writer.append(csq, start, end);
                return this;
            }

            @Override
            public CloseableAppendable append(char c) throws IOException {
                writer.append(c);
                return this;
            }
        };
    }
}
