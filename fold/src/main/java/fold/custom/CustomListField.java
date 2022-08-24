package fold.custom;

import fold.adapter.Adapter;

import java.util.*;

public class CustomListField<T, V> {
    private final V[] names;
    private final String namespace;
    private final Adapter<Map<V, Object>, T> factory;

    public CustomListField(String namespace, V[] names, Adapter<Map<V, Object>, T> factory) {
        this.names = names;
        this.namespace = namespace;
        this.factory = factory;
    }

    public List<T> getValue(Map<String, Object> customMap) {
        Map<V, List<?>> vals = new HashMap<>();
        for (V name : names) {
            vals.put(name, (List<?>) customMap.get(getKey(name)));
        }

        int size = 0;

        for (List<?> val : vals.values()) {
            if (val == null) continue;
            if (val.size() > size) {
                size = val.size();
            }
        }

        List<T> out = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Map<V, Object> constructorMap = new HashMap<>();

            for (V v : vals.keySet()) {
                List<?> val = vals.get(v);

                if (val == null) continue;

                Object value = vals.get(v).size() > i ? vals.get(v).get(i) : null;
                constructorMap.put(v, value);
            }

            T apply = null;
            try {
                apply = factory.convert(constructorMap, null);
            } catch (Exception e) {
                // ignore
            }

            if (apply != null) {
                out.add(apply);
            }
        }

        return out;
    }

    private String getKey(V name) {
        return namespace + ":" + name;
    }

    public void setValue(Map<String, Object> customMap, List<T> val) {
        Map<String, List<Object>> tempMap = new HashMap<>();
        for (V v : names) {
            tempMap.put(getKey(v), new ArrayList<>());
        }

        for (T t : val) {
            Map<V, Object> map = factory.convertBack(t, new HashMap<>());

            for (V v : map.keySet()) {
                tempMap.get(getKey(v)).add(map.get(v));
            }
        }

        customMap.putAll(tempMap);
    }
}
