package fold.adapter;

public interface Adapter<T, V> {
    V convert(T from, V to);

    T convertBack(V from, T to);
}
