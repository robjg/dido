package dido.how.useful;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataOut;
import dido.how.RefinableOutHow;

public class UnknownDataOut<O> implements DataOut {

    private final O outTo;

    private final RefinableOutHow<O> how;

    private DataOut known;

    private UnknownDataOut(final O outTo, RefinableOutHow<O> how) {
        this.outTo = outTo;
        this.how = how;
    }

    public static <O> UnknownDataOut<O> outToOf(O outTo, RefinableOutHow<O> how) {
        return new UnknownDataOut<>(outTo, how);
    }

    @Override
    public void close() {
        if (known == null) {
            known = how.forSchema(DataSchema.emptySchema())
                    .outTo(outTo);
        }

        known.close();
    }

    @Override
    public void accept(DidoData didoData) {
        if (known == null) {
            known = how.forSchema(didoData.getSchema())
                    .outTo(outTo);
        }

        known.accept(didoData);
    }

    @Override
    public String toString() {
        return "Unknown of " + how.toString();
    }

}
