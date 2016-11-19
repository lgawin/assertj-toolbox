package pl.lgawin.testbox.assertj;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;


public class InterfaceComparator<T> implements Comparator<Object> {

    private final Class<T> clazz;
    private final Method[] methods;

    public InterfaceComparator(Class<T> clazz) {
        checkArgument(clazz.isInterface(), "Expected interface but got class: %s", clazz.getCanonicalName());
        this.clazz = clazz;
        this.methods = clazz.getMethods();
        checkArgument(methods.length > 0, "Expected interface %s to contain some methods", clazz.getCanonicalName());
        checkArgument(Stream.of(methods).allMatch(new Predicate<Method>() {
                    @Override
                    public boolean test(Method method) {
                        return method.getParameterTypes().length == 0;
                    }
                }), "Expected interface %s to contain non-parameters methods only", clazz.getCanonicalName()
//TODO nice-to-have: method signature
//                        + ", but found: " + ""
        );
    }

    @Override
    public int compare(final Object o1, final Object o2) {
        checkArgument(clazz.isAssignableFrom(o1.getClass()), "Expected class %s but got %s",
                clazz.getCanonicalName(), o1.getClass().getCanonicalName());
        checkArgument(clazz.isAssignableFrom(o2.getClass()), "Expected class %s but got %s",
                clazz.getCanonicalName(), o2.getClass().getCanonicalName());

        if (isComparableType(clazz)) {
            //noinspection unchecked
            return castComparable(o1).compareTo(o2);
        }

        return Stream.of(methods)
                .map(new Function<Method, Integer>() {
                    @Override
                    public Integer apply(Method method) {
                        return compareMethod(o1, o2, method);
                    }
                })
                .reduce(0, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer value1, Integer value2) {
                        return value1 == 0 ? value2 : value1;
                    }
                });
    }

    private int compareMethod(Object o1, Object o2, Method method) {
        try {
            Object result1 = method.invoke(o1);
            Object result2 = method.invoke(o2);

            if (result1 == null && result2 == null) {
                return 0;
            }
            if (isOfComparableType(result1) && isOfComparableType(result2)) {
                //noinspection unchecked
                return castComparable(result1).compareTo(result2);
            }
            return Objects.equals(result1, result2) ? 0 : -1;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -2;
    }

    private Comparable castComparable(Object o1) {
        return Comparable.class.cast(o1);
    }

    private boolean isOfComparableType(Object object) {
        return object != null && isComparableType(object.getClass());
    }

    private boolean isComparableType(Class<?> aClass) {
        return Comparable.class.isAssignableFrom(aClass);
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }
}
