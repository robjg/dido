package dido.how;

import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;

class DataInTest {

    DidoData[] data = new DidoData[]{
            mock(DidoData.class), mock(DidoData.class), null};


    @Test
    void iterable() throws Exception {

        List<DidoData> copy = new ArrayList<>();

        try (DataIn dataIn = DataIn.of(data)) {

            for (DidoData data : dataIn) {
                copy.add(data);
            }
        }

        assertThat(copy, contains(data[0], data[1]));
    }

    @Test
    void stream() throws Exception {

        try (DataIn in = DataIn.of(data)) {

            List<DidoData> copy = in.stream().collect(Collectors.toList());

            assertThat(copy, contains(data[0], data[1]));
        }
    }

}