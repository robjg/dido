package dido.oddjob.beanbus;

import dido.data.DidoData;

import java.util.function.Function;

/**
 * @oddjob.description Provide a BeanBus component that uses a mapper to convert to
 * {@link DidoData} from some other data type.
 * <p>
 *
 * @oddjob.example From and To lines of text.
 * {@oddjob.xml.resource dido/oddjob/beanbus/MapInOut.xml}
 *
 * @see FromDido
 *
 * @param <F> The type of data being mapped from.
 */
public class ToDido<F> extends AbstractDataMapper<F, DidoData> {

    @Override
    public void setFunction(Function<F, DidoData> function) {
        super.setFunction(function);
    }


}
