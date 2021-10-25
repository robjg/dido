package dido.oddjob.transpose;

import dido.data.DataSchema;
import org.oddjob.arooa.types.ValueFactory;

public class DataCopy implements ValueFactory<TransposerFactory<String, String>> {

    private String field;

    private int index;

    private String to;

    private int at;

    @Override
    public TransposerFactory<String, String> toValue() {
        return new CopyTransposerFactory(this);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAt() {
        return at;
    }

    public void setAt(int at) {
        this.at = at;
    }

    static class CopyTransposerFactory implements TransposerFactory<String, String> {

        private final String from;

        private final String to;

        private final int index;

        private final int at;

        CopyTransposerFactory(DataCopy from) {
            this.from = from.field;
            this.to = from.to;
            this.index = from.index;
            this.at = from.at;
        }

        @Override
        public Transposer<String, String> create(int position,
                                               DataSchema<String> fromSchema,
                                               SchemaSetter<String> schemaSetter) {

            String to;
            int at;

            if (this.at == 0) {
                if (this.index == 0) {
                    if (this.from == null) {
                        at = position;
                    }
                    else {
                        at = fromSchema.getIndex(this.from);
                    }
                }
                else {
                    at = this.index;
                }
            }
            else {
                at = this.at;
            }

            int index;
            if (this.index == 0) {
                index = position;
            }
            else {
                index = this.index;
            }

            Transposer<String, String> transposer = (from, into) -> into.setAt(at, from.getAt(index));

            if (this.to == null) {
                if (this.from == null) {
                    if (this.index == 0) {
                        to = null;
                    }
                    else {
                        to = fromSchema.getFieldAt(this.index);
                    }
                }
                else {
                    to = this.from;
                    transposer = (from, into) -> into.setAt(at, from.get(this.from));
                }
            }
            else {
                to = this.to;
                if (this.from == null) {
                    transposer = (from, into) -> into.set(to, from.getAt(index));
                }
                else {
                    transposer = (from, into) -> into.set(to, from.get(this.from));
                }
            }


            schemaSetter.setFieldAt(at, to, fromSchema.getTypeAt(index));

            return transposer;
        }
    }

}
