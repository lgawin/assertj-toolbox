package pl.lgawin.testbox.assertj;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class InterfaceComparatorCreationTest {

    private static class SomeClass {
        // EMPTY
    }

    @Test
    public void shouldRefuseUsingClassForComparator() {
        // given
        final Class<SomeClass> classForComparator = SomeClass.class;

        // when
        Throwable throwable = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                new InterfaceComparator<SomeClass>(classForComparator);
            }
        });

        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected interface but got class: " + classForComparator.getCanonicalName());
    }

    interface EmptyInterface {
        // EMPTY
    }

    @Test
    public void shouldRefuseEmptyInterfaceComparison() {
        // given
        final Class<EmptyInterface> emptyInterfaceClass = EmptyInterface.class;

        // when
        Throwable throwable = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                new InterfaceComparator<EmptyInterface>(emptyInterfaceClass);
            }
        });

        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected interface " + emptyInterfaceClass.getCanonicalName() + " to contain some methods");
    }

    interface InterfaceWithIllegalMethod {

        String legal();
        int illegal(String argument);
    }

    @Test
    public void shouldRefuseInterfaceWithParametrizedMethod() {
        // given
        final Class<InterfaceWithIllegalMethod> interfaceClass = InterfaceWithIllegalMethod.class;

        // when
        Throwable throwable = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                new InterfaceComparator<InterfaceWithIllegalMethod>(interfaceClass);
            }
        });

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected interface " + interfaceClass.getCanonicalName()
                                + " to contain non-parameters methods only"
//TODO nice-to-have
//                        + ", but found: "
//                        + "int illegal(String)"
                );
    }

    interface ExtendingIllegalInterface extends InterfaceWithIllegalMethod {
        // non-illegal (as the name suggests), intentionally clashing with parent method
        int illegal();
    }

    @Test
    public void shouldRefuseInterfaceExtendingTheOneWithIllegalMethod() {
        // given
        final Class<ExtendingIllegalInterface> interfaceClass = ExtendingIllegalInterface.class;

        // when
        Throwable throwable = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                new InterfaceComparator<ExtendingIllegalInterface>(interfaceClass);
            }
        });

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected interface " + interfaceClass.getCanonicalName()
                                + " to contain non-parameters methods only"
//TODO nice-to-have
//                        + ", but found: "
//                        + "int illegal(String)"
                );
    }

    interface SimpleInterface {
        int value();
    }

    interface EmptyExtensionToNonEmptyInterface extends SimpleInterface {
        // NOP
    }

    @Test
    public void shouldAllowEmptyInterfaceThatExtendsNonEmptyInterface() {
        // given
        final Class<EmptyExtensionToNonEmptyInterface> interfaceClass = EmptyExtensionToNonEmptyInterface.class;

        // when
        Throwable throwable = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                new InterfaceComparator<EmptyExtensionToNonEmptyInterface>(interfaceClass);
            }
        });

        // then
        assertThat(throwable).isNull();
    }

    interface EmptyExtensionToToEmptyInterface extends EmptyInterface {
        // NOP
    }

    @Test
    public void shouldRefuseEmptyInterfaceThatExtendsAnotherEmptyInterface() {
        // given
        final Class<EmptyExtensionToToEmptyInterface> interfaceClass = EmptyExtensionToToEmptyInterface.class;

        // when
        Throwable throwable = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                new InterfaceComparator<EmptyExtensionToToEmptyInterface>(interfaceClass);
            }
        });

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected interface " + interfaceClass.getCanonicalName() + " to contain some methods");
    }
}