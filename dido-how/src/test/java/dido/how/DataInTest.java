package dido.how;

import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class DataInTest {

    DidoData[] data = (DidoData[]) new DidoData[]{
            mock(DidoData.class), mock(DidoData.class), null};

    AtomicBoolean closed = new AtomicBoolean();

    class SomeDataIn implements DataIn<DidoData> {

        int index = 0;

        @Override
        public void close() {
            closed.set(true);
        }

        @Override
        public DidoData get() {
            return data[index++];
        }
    }

    @Test
    void iterable() throws Exception {

        List<DidoData> copy = new ArrayList<>();

        try (DataIn<DidoData> dataIn = new SomeDataIn()) {

            for (DidoData data : dataIn) {
                copy.add(data);
            }
        }

        assertThat(copy, contains(data[0], data[1]));

        assertThat(closed.get(), is(true));
    }

    @Test
    void stream() throws Exception {

        try (DataIn<?> in = new SomeDataIn()) {

            List<DidoData> copy = in.stream().collect(Collectors.toList());

            assertThat(copy, contains(data[0], data[1]));

        }
        assertThat(closed.get(), is(true));
    }

    @Test
    void iterableDoestMoveOnUntilHasNext() {

        DidoData data1 = mock(DidoData.class);
        DidoData data2 = mock(DidoData.class);

        DataIn<DidoData> dataIn = mock(DataIn.class);
        when(dataIn.get()).thenReturn(data1)
                .thenReturn(data2)
                .thenReturn(null);

        when(dataIn.iterator()).thenCallRealMethod();

        Iterator<DidoData> it = dataIn.iterator();

        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is(data1));

        verify(dataIn, times(1)).get();

        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is(data2));

        verify(dataIn, times(2)).get();

        assertThat(it.hasNext(), is(false));

        verify(dataIn, times(3)).get();
    }
}