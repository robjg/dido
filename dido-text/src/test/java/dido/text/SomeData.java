package dido.text;

import dido.data.GenericData;
import dido.data.MapData;
import org.oddjob.arooa.types.ValueFactory;

import java.util.List;

public class SomeData implements ValueFactory<List<GenericData<String>>> {

    @Override
    public List<GenericData<String>> toValue() {

        return List.of(
                MapData.of("Fruit", "Apple", "Quantity", 5, "Price", 22.3),
                MapData.of("Fruit", "Cantaloupe", "Quantity", 27, "Price", 245.3),
                MapData.of("Fruit", "Pear", "Quantity", 232, "Price", 11.328)
        );
    }
}
