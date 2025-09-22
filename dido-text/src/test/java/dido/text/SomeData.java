package dido.text;

import dido.data.DidoData;
import dido.data.immutable.MapData;
import org.oddjob.arooa.types.ValueFactory;

import java.util.List;

public class SomeData implements ValueFactory<List<DidoData>> {

    @Override
    public List<DidoData> toValue() {

        return List.of(
                MapData.of("Fruit", "Apple", "Quantity", 5, "Price", 22.3),
                MapData.of("Fruit", "Cantaloupe", "Quantity", 27, "Price", 245.3),
                MapData.of("Fruit", "Pear", "Quantity", 232, "Price", 11.328)
        );
    }
}
