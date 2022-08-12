package fold.custom;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomField<T, V extends Enum<V>> {
    private List<V> names;
    private String namespace;
    private Function<Map<V, Object>, T> factory;

    public CustomField() {

    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public T getValue(Map<String, Object> customMap) {
        Map<V, Object> vals = names.stream().collect(Collectors.toMap(v -> v, v -> customMap.get(namespace + ":" + v.toString())));
        return factory.apply(vals);
    }

    public void setNames(List<V> names) {
        this.names = names;
    }
}
