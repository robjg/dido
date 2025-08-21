package dido.oddjob.bean;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Used to fail in Java 8. Now fixed:
 * <a href=https://stackoverflow.com/questions/31703563/java-8-interface-default-method-doesnt-seem-to-declare-property">See this</a>
 */
class DefaultMethodAssumptionsTest {

    public interface Foo {

        default String getFoo() {
            return "Foo";
        }
    }

    public static class Bar implements Foo {

    }

    @Test
    void defaultMethodsCanBeProperties() {

        PropertyAccessor propertyAccessor = new BeanUtilsPropertyAccessor();

        Foo foo = new Bar();

        assertThat(propertyAccessor.getProperty(foo, "foo"),
                is("Foo"));
    }

}
