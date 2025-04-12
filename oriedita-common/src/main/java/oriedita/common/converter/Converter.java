package oriedita.common.converter;

/**
 * Converts between 2 types of values, typically for ui content binding
 * @param <T> origin data type
 * @param <C> display type (usually string)
 */
public interface Converter<T, C> {
    C convert(T value);
    T convertBack(C value);
    boolean canConvert(T value);
    boolean canConvertBack(C value);
}
