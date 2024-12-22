package dido.how.conversion;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequiringConversionTest {

    @Test
    void noConversionThrowsException() {

        DidoConversionProvider conversionProvider = DefaultConversionProvider.defaultInstance();

        assertThat(assertThrows(NoConversionException.class,
                () -> RequiringConversion.with(conversionProvider)
                        .from(Object.class).to(Double.class))
                .getMessage(), containsString("No conversion"));
    }
}