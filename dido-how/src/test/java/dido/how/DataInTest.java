package dido.how;

import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class DataInTest {

    @Test
    void iterable() throws Exception {


        @SuppressWarnings("unchecked")
        DidoData[] data = (DidoData[]) new DidoData[] {
                mock(DidoData.class), mock(DidoData.class), null };

        try (DataIn dataIn = new DataIn() {
                    int index = 0;

                    @Override
                    public void close() {

                    }

                    @Override
                    public DidoData get() {
                        return data[index++];
                    }
                }) {

            List<DidoData> copy = StreamSupport.stream(dataIn.spliterator(), false)
                    .collect(Collectors.toList());

            assertThat(copy, contains(data[0], data[1]));

        }
    }

    @Test
    void iterableDoestMoveOnUntilHasNext() {

        DidoData data1 = mock(DidoData.class);
        DidoData data2 = mock(DidoData.class);

        DataIn dataIn = mock(DataIn.class);
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