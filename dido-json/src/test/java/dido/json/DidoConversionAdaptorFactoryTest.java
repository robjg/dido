package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DidoConversionAdaptorFactoryTest {

    @Test
    void didoConversionUsed() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .<Integer, LocalDate>conversion(Integer.class, LocalDate.class,
                        i -> LocalDate.parse(Integer.toString(i),
                                DateTimeFormatter.ofPattern("yyyyMMdd")))
                .<LocalDate, Integer>conversion(LocalDate.class, Integer.class,
                        d -> Integer.parseInt(
                                d.format(DateTimeFormatter.ofPattern("yyyyMMdd"))))
                .make();

        DidoConversionAdaptorFactory test = DidoConversionAdaptorFactory.with()
                .conversionProvider(conversionProvider)
                .register(Integer.class, LocalDate.class)
                .make();

        assertThat(test.isEmpty(), is(false));

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(test)
                .create();

        LocalDate result = gson.fromJson("20250129", LocalDate.class);

        assertThat(result, is(LocalDate.parse("2025-01-29")));

        String back = gson.toJson(result);

        assertThat(back, is("20250129"));
    }
}