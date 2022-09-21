package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class GsonAssumptionsTest {

    @Test
    void testPrimitiveArrays() {

        String json = "[1,2,3,4]";

        Gson gson = new GsonBuilder().create();

        assertThat(gson.fromJson(json, double[].class), is(new double[]{1.0, 2.0, 3.0, 4.0}));
        assertThat(gson.fromJson(json, int[].class), is(new int[]{1, 2, 3, 4}));
        assertThat(gson.fromJson(json, long[].class), is(new long[]{1L, 2L, 3L, 4L}));

        assertThat(gson.fromJson(json, Object.class), is(List.of(1.0, 2.0, 3.0, 4.0)));

        assertThat(gson.fromJson(json, Object[].class), is( new Object[] { 1.0, 2.0, 3.0, 4.0 }));
    }

}
