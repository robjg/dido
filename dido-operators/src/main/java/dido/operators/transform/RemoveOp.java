package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.SchemaField;

public interface RemoveOp {

    SchemaField remove(DataSchema incomingSchema);

    default OpDef asOpDef() {

        return (incomingSchema, schemaSetter) -> {
            schemaSetter.removeField(remove(incomingSchema));
            return (OpDef.Prepare) writeSchema ->
                    (incomingData, writeableDate) -> {
                    };
        };
    }


}
