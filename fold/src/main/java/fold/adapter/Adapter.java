package fold.adapter;

public interface Adapter<T, V> {
    V convert(T from);
    T convertBack(V from);
}
