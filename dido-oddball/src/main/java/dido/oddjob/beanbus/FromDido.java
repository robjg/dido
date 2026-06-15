package dido.oddjob.beanbus;

import dido.data.DidoData;

import java.util.function.Function;

/**
 * @oddjob.description Provide a BeanBus component that uses a mapper to convert from
 * a {@link DidoData} to some other data type.
 * <p>
 * Still a concept in progress. BeanBus already has a {@code bus:map} that takes
 * a function. This however allows conversion by Generic Types. Alternatives to this
 * approach might be some sort of dynamic conversion either wrapping the mapper with
 * a {@code convert} tag or specifying the type in the {@code bus:map} component.
 * The main purpose is to allow a single bean definition to provide both in and out functions
 * so they may be shared across an application, as in the simple example below.
 *
 * @oddjob.example From and To lines of text.
 * {@oddjob.xml.resource dido/oddjob/beanbus/MapInOut.xml}
 *
 * @see ToDido
 *
 * @param <T> The type of data being mapped to.
 */
public class FromDido<T> extends AbstractDataMapper<DidoData, T> {

    @Override
    public void setFunction(Function<DidoData, T> function) {
        super.setFunction(function);
    }
}
