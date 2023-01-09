package dido.replay;

import dido.data.GenericData;
import dido.data.MapData;
import dido.how.CloseableConsumer;
import dido.how.CloseableSupplier;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class DataRecordReplayTest {

    @Test
    void givenDataThenRecords() throws Exception {

        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        ByteArrayOutputStream schemaOut = new ByteArrayOutputStream();
        ByteArrayOutputStream timeOut = new ByteArrayOutputStream();

        CloseableConsumer<GenericData<String>> recorder = DataRecorder.withSettings()
                .dataOut(dataOut)
                .schemaOut(schemaOut)
                .timeOut(timeOut)
                .make();

        GenericData<String> data = MapData.of("Fruit", "Applie", "Qty", 5);

        recorder.accept(data);

        recorder.close();

        ByteArrayInputStream dataIn = new ByteArrayInputStream(dataOut.toByteArray());
        ByteArrayInputStream schemaIn = new ByteArrayInputStream(schemaOut.toByteArray());
        ByteArrayInputStream timeIn = new ByteArrayInputStream(timeOut.toByteArray());

        CloseableSupplier<DataPlayer.TimedData> player = DataPlayer.withSettings()
                .dataIn(dataIn)
                .schemaIn(schemaIn)
                .timeIn(timeIn)
                .make();

        DataPlayer.TimedData back = player.get();

        assertThat(player.get(), nullValue());

        player.close();

        assertThat(back.getData(), is(data));

    }

}