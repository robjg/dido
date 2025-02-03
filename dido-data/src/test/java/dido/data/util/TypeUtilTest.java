package dido.data.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TypeUtilTest {

    abstract static class PoorMansTypeToken<T> {

        final ParameterizedType type;

        PoorMansTypeToken() {
            ParameterizedType ourType = (ParameterizedType) getClass().getGenericSuperclass();
            type = (ParameterizedType) ourType.getActualTypeArguments()[0];
        }
    }

    @Test
    void withParameterizedType() {

        Type type = new PoorMansTypeToken<List<String>>() {}.type;
        Type type2 = new PoorMansTypeToken<ArrayList<String>>() {}.type;

        assertThat(type.getTypeName(), is("java.util.List<java.lang.String>"));
        assertThat(TypeUtil.classOf(type), is(List.class));
        assertThat(TypeUtil.isAssignableFrom(List.class, type), is(true));
        assertThat(TypeUtil.isAssignableFrom(type, type2), is(false));
    }

    @Test
    void withNormalType() {

        Type type = List.class;

        assertThat(type.getTypeName(), is("java.util.List"));
        assertThat(TypeUtil.classOf(type), is(List.class));
        assertThat(TypeUtil.isAssignableFrom(type, ArrayList.class), is(true));
    }
}