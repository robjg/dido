package dido.data.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ClassUtilsTest {

    @Test
    void differentType() throws ClassNotFoundException {

        assertThat(classFor(int.class), is(int.class));
        assertThat(classFor(void.class), is(void.class));
        assertThat(classFor(String.class), is(String.class));
    }

    Class<?> classFor(Class<?> in) throws ClassNotFoundException {

        return ClassUtils.classFor(in.getName(), getClass().getClassLoader());
    }
}