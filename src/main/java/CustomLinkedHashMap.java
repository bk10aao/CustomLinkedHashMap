import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.isInstanceOf;

public class CustomLinkedHashMap<K, V> {

    private Map<K, Node<K, V>> map;

    private transient Node<K, V> head;
    private transient Node<K, V> tail;

    private Class<K> key;
    private Class<V> value;

    public CustomLinkedHashMap(final Class<K> key, final Class<V> value) {
        this(key, value, 0.75f);
    }

    public CustomLinkedHashMap(final Class<K> key, final Class<V> value, float loadFactor) {
        if (loadFactor <= 0 || loadFactor > 1)
            throw new IllegalArgumentException("Invalid load factor");
        this.key = key;
        this.value = value;
        this.map = new HashMap<>();
    }

    public void clear() {
        head = tail = null;
        map = new HashMap<>();
    }

    public boolean containsKey(K key) {
        requireNonNull(key);
        return map.containsKey(key);
    }

    public boolean containsValue(V value) {
        isInstanceOf(this.value, value, "Key value does not match");
        Node<K, V> current = head;
        while (current != null) {
            if (Objects.equals(current.value, value))
                return true;
            current = current.next;
        }
        return false;
    }

    public V get(K key) {
        if(key != null)
            isInstanceOf(this.key, key, "Key value does not match");
        Node<K, V> node = map.get(key);
        return node != null ? node.getValue() : null;
    }

    public V getOrDefault(final K key, final V defaultValue) {
        requireNonNull(key, "Key value must not be null.");
        V returnValue = get(key);
        return  returnValue != null ? returnValue : defaultValue;
    }

    public V put(K key, V value) {
        if(key == null)
            throw new IllegalArgumentException();
        isInstanceOf(this.key, key, "Key value does not match");
        Node<K, V> node = map.get(key);
        if(node != null) {
            V previousValue = node.value;
            node.value = value;
            return previousValue;
        }
        node = new Node<>(key, value);
        map.put(key, node);
        if (tail == null)
            head = tail = node;
        else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        return null;
    }

    public V putIfAbsent(final K key, final V value) {
        requireNonNull(key);
        requireNonNull(value);
        Node<K, V> node = map.get(key);
        if(node != null)
            return node.value;
        put(key, value);
        return null;
    }

    public V remove(Object key) {
        requireNonNull(key);
        Node<K, V> node = map.remove(key);
        if(node == null)
            return null;
        V value = node.value;
        if(node == head)
            head = node.next;
        else
            node.prev.next = node.next;
        if(node == tail)
            tail = node.prev;
        else
            node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
        return value;
    }

    public boolean remove(final K key, final V value) {
        requireNonNull(key);
        requireNonNull(value);
        Node<K, V> node = map.get(key);
        if (node == null || !Objects.equals(node.value, value))
            return false;
        remove(key);
        return true;
    }

    public int size() {
        return map.size();
    }

    public Set<K> keySet() {
        return map.keySet();
    }


    private static class Node<K, V> implements Map.Entry<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V old = value;
            value = newValue;
            return old;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }
}
