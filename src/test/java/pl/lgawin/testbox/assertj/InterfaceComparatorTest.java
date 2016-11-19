package pl.lgawin.testbox.assertj;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InterfaceComparatorTest {

    private final InterfaceComparator<SampleInterface> comparatorSample =
            new InterfaceComparator<SampleInterface>(SampleInterface.class);

    private final SampleInterface oneAnonymous = anonymous("One");
    private final ComparableSampleInterface oneComparableAnonymous = anonymousComparable("One", 1);
    private final MySampleInterface oneSimple = new MySampleInterface("One");
    private final MySampleInterface twoSimple = new MySampleInterface("Two");
    private final MyComparableSampleInterface twoComparableSimple = new MyComparableSampleInterface("Two");

    private interface SingleMethodInterface {

        String string();
    }

    @Test
    public void shouldReportEqualInterfacesIfBothReturnNull() {
        // given
        InterfaceComparator<SingleMethodInterface> comparator =
                new InterfaceComparator<SingleMethodInterface>(SingleMethodInterface.class);
        SingleMethodInterface left = new SingleMethodInterface() {
            @Override
            public String string() {
                return null;
            }
        };
        SingleMethodInterface right = new SingleMethodInterface() {
            @Override
            public String string() {
                return null;
            }
        };
        checkState(!left.equals(right));

        // when
        int result = comparator.compare(left, right);

        // then
        assertThat(result).isZero();
    }

    @Test
    public void shouldReportNotEqualInterfacesIfOnlyOneReturnsNull() {
        // given
        InterfaceComparator<SingleMethodInterface> comparator =
                new InterfaceComparator<SingleMethodInterface>(SingleMethodInterface.class);
        SingleMethodInterface left = new SingleMethodInterface() {
            @Override
            public String string() {
                return null;
            }
        };
        SingleMethodInterface right = new SingleMethodInterface() {
            @Override
            public String string() {
                return "some value";
            }
        };

        // when
        int result = comparator.compare(left, right);

        // then
        assertThat(result).isNotZero();
    }

    @Test
    public void shouldReportNotEqualForTwoSameInterfacesReturningDifferentValues() {
        // given
        InterfaceComparator<SingleMethodInterface> comparator =
                new InterfaceComparator<SingleMethodInterface>(SingleMethodInterface.class);
        SingleMethodInterface left = new SingleMethodInterface() {
            @Override
            public String string() {
                return "some value";
            }
        };
        SingleMethodInterface right = new SingleMethodInterface() {
            @Override
            public String string() {
                return "other value";
            }
        };

        // when
        int result = comparator.compare(left, right);

        // then
        assertThat(result).isNotZero();
    }

    @Test
    public void shouldReportEqualForTwoSameInterfacesReturningSameValues() {
        // given
        InterfaceComparator<SingleMethodInterface> comparator =
                new InterfaceComparator<SingleMethodInterface>(SingleMethodInterface.class);
        SingleMethodInterface left = new SingleMethodInterface() {
            @Override
            public String string() {
                return "some value";
            }
        };
        SingleMethodInterface right = new SingleMethodInterface() {
            @Override
            public String string() {
                return "some value";
            }
        };

        // when
        int result = comparator.compare(left, right);

        // then
        assertThat(result).isZero();
    }

    private static class Value {
        int value;

        public Value(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Value value1 = (Value) o;

            return value == value1.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    private interface NonComparableWithEqualsReturning {

        Value someMethod();
    }

    @Test
    public void shouldReportEqualForNonComparableReturningInterfacesWithTheSameValueWithEqualsImplemented() {
        // given
        InterfaceComparator<NonComparableWithEqualsReturning> comparator =
                new InterfaceComparator<NonComparableWithEqualsReturning>(NonComparableWithEqualsReturning.class);
        NonComparableWithEqualsReturning left = new NonComparableWithEqualsReturning() {
            @Override
            public Value someMethod() {
                return new Value(1);
            }
        };
        NonComparableWithEqualsReturning right = new NonComparableWithEqualsReturning() {
            @Override
            public Value someMethod() {
                return new Value(1);
            }
        };

        // when
        int result = comparator.compare(left, right);

        // then
        assertThat(result).isZero();
    }

    @Test
    public void twoDifferenceInstancesOfInterfaceWithSameValuesReturnedAreEqual() {
        // when
        int compare = comparatorSample.compare(oneAnonymous, oneSimple);

        // then
        assertThat(compare).isZero();
    }

    @Test
    @Ignore
    public void twoDifferenceInstancesOfComparableInterfaceWithSameValuesReturnedAreEqual() {
        //given
        InterfaceComparator<ComparableSampleInterface> comparatorComparable =
                new InterfaceComparator<ComparableSampleInterface>(ComparableSampleInterface.class);

        // when
        int compare = comparatorComparable.compare(oneComparableAnonymous, twoComparableSimple);

        // then
        assertThat(compare).isNotZero();
    }

    @Test
    public void twoDifferenceInstancesOfInterfaceWithDifferentValuesReturnedAreNotEqual() {
        // when
        int compare = comparatorSample.compare(oneAnonymous, twoSimple);

        // then
        assertThat(compare).isNotZero();
    }

    @Test
    public void test() {
        // when
        int compare = comparatorSample.compare(oneComparableAnonymous, twoComparableSimple);

        // then
        assertThat(compare).isNotZero();
    }

    @Test
    public void test2() {
        // when
        int compare = comparatorSample.compare(oneComparableAnonymous, anonymousComparable("One", 1));

        // then
        assertThat(compare).isZero();
    }

    @Test
    public void test3() {
        // when
        int compare = comparatorSample.compare(oneComparableAnonymous, anonymousComparable("One", 2));

        // then
        assertThat(compare).isNotZero();
    }

    @Test
    @Ignore("Implement short circuit break")
    public void test4() {
        // when
        int compare = comparatorSample.compare(oneComparableAnonymous, new MyComparableSampleInterface("Two") {
            @Override
            public int value() {
                try {
                    java.util.concurrent.TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                throw new UnsupportedOperationException("Not implemented");
            }
        });

        // then
        assertThat(compare).isNotZero();
    }

    @Test
    @Ignore
    public void twoDifferenceInstancesOfComparableInterfaceWithDifferentValuesReturnedAreNotEqual() {
        //given
        InterfaceComparator<ComparableSampleInterface> comparatorComparable =
                new InterfaceComparator<ComparableSampleInterface>(ComparableSampleInterface.class);

        // when
        int compare = comparatorComparable.compare(oneComparableAnonymous, twoComparableSimple);

        // then
        assertThat(compare).isNotZero();
    }

    private SampleInterface anonymous(final String name) {
        return new SampleInterface() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public int value() {
                return 0;
            }
        };
    }

    private ComparableSampleInterface anonymousComparable(final String name, final int value) {
        return new ComparableSampleInterface() {
            @Override
            public int foo() {
                return 0;
            }

            @Override
            public int compareTo(SampleInterface o) {
                return name().compareTo(o.name());
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public int value() {
                return value;
            }
        };
    }

    interface SampleInterface {
        String name();

        int value();
    }

    interface ComparableSampleInterface extends SampleInterface, Comparable<SampleInterface> {
        int foo();
    }

    private static class MySampleInterface implements SampleInterface {
        private final String name;

        public MySampleInterface(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int value() {
            return 0;
        }
    }

    private static class MyComparableSampleInterface implements ComparableSampleInterface {
        private final String name;

        public MyComparableSampleInterface(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int value() {
            return 0;
        }

        @Override
        public int compareTo(SampleInterface o) {
            return name().compareTo(o.name());
        }

        @Override
        public int foo() {
            return 0;
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
}