import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Benjamin Kane
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @see <a href="https://www.linkedin.com/in/benjamin-kane-81149482/">LinkedIn</a>
 * @see <a href="https://github.com/bk10aao">GitHub account bk10aao</a>
 * @see <a href="https://github.com/bk10aao/CustomLinkedHashMap">Repository</a>
 */
public class CustomLinkedHashMap<K, V> implements Map<K, V>{

    private final boolean accessOrder;

    private final int maxEntries;

    private final Map<K, Node<K, V>> map;

    private transient Node<K, V> head;
    private transient Node<K, V> tail;

    private final Class<K> key;
    private final Class<V> value;

    public CustomLinkedHashMap(final Class<K> key, final Class<V> value) {
        this(key, value, Integer.MAX_VALUE, 0.75f, false);
    }

    public CustomLinkedHashMap(final Class<K> key, final Class<V> value, int maxEntries) {
        this(key, value, maxEntries, 0.75f, false);
    }

    public CustomLinkedHashMap(final Class<K> key, final Class<V> value, int maxEntries, final boolean accessOrder) {
        this(key, value, maxEntries, 0.75f, accessOrder);
    }

    public CustomLinkedHashMap(final Class<K> key, final Class<V> value, float loadFactor) {
        this(key, value, Integer.MAX_VALUE, loadFactor, false);
    }

    public void clear() {
        head = tail = null;
        map.clear();
    }

    public boolean containsKey(final Object key) {
        if(!this.key.isInstance(key))
            return false;
        return map.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        if(!this.value.isInstance(value))
            return false;
        Node<K, V> current = head;
        while(current != null) {
            if(Objects.equals(current.value, value))
                return true;
            current = current.next;
        }
        return false;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return new java.util.AbstractSet<>() {
            @Override
            public int size() {
                return CustomLinkedHashMap.this.size();
            }

            @Override
            public java.util.Iterator<Map.Entry<K, V>> iterator() {
                return new java.util.Iterator<>() {

                    private Node<K, V> current = head;
                    private Node<K, V> lastReturned = null;

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }

                    @Override
                    public Map.Entry<K, V> next() {
                        if(!hasNext())
                            throw new java.util.NoSuchElementException();
                        lastReturned = current;
                        current = current.next;
                        return lastReturned;
                    }

                    @Override
                    public void remove() {
                        if(lastReturned == null)
                            throw new IllegalStateException();
                        CustomLinkedHashMap.this.remove(lastReturned.key);
                        lastReturned = null;
                    }
                };
            }
        };
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o)
            return true;
        if(!(o instanceof Map<?, ?> otherMap))
            return false;
        if(size() != otherMap.size())
            return false;
        if(o instanceof CustomLinkedHashMap<?, ?> otherCustom)
            if(!Objects.equals(this.key, otherCustom.key) || !Objects.equals(this.value, otherCustom.value))
                return false;
        for(Map.Entry<K, V> entry : entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if(!otherMap.containsKey(key))
                return false;
            Object otherValue = otherMap.get(key);
            if(!Objects.equals(value, otherValue))
                return false;
        }
        return true;
    }

    public V get(final Object key) {
        requireNonNull(key);
        if(!this.key.isInstance(key))
            return null;
        Node<K, V> node = map.get(key);
        if(node == null)
            return null;
        if(accessOrder)
            moveToTail(node);
        return node.getValue();
    }

    public V getOrDefault(final Object key, final V defaultValue) {
        requireNonNull(key);
        if(!this.key.isInstance(key))
            return defaultValue;
        Node<K, V> node = map.get(key);
        if(node == null)
            return defaultValue;
        if(accessOrder)
            moveToTail(node);
        return  node.getValue();
    }

    @Override
    public int hashCode() {
        int h = 0;
        for(Map.Entry<K, V> entry : entrySet())
            h += entry.hashCode();
        return h;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set<K> keySet() {
        return new java.util.AbstractSet<>() {
            @Override
            public int size() {
                return CustomLinkedHashMap.this.size();
            }

            @Override
            public java.util.Iterator<K> iterator() {
                return new java.util.Iterator<>() {
                    private Node<K, V> current = head;
                    private Node<K, V> lastReturned = null;

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }

                    @Override
                    public K next() {
                        if(!hasNext())
                            throw new java.util.NoSuchElementException();
                        lastReturned = current;
                        current = current.next;
                        return lastReturned.key;
                    }

                    @Override
                    public void remove() {
                        if(lastReturned == null)
                            throw new IllegalStateException();
                        CustomLinkedHashMap.this.remove(lastReturned.key);
                        lastReturned = null;
                    }
                };
            }
        };
    }

    public V put(final K key, final V value) {
        if(key == null)
            throw new IllegalArgumentException();
        if(!this.key.isInstance(key) || value != null && !this.value.isInstance(value))
            throw new ClassCastException();
        Node<K, V> node = map.get(key);
        if(node != null) {
            V previousValue = node.value;
            node.value = value;
            if(accessOrder)
                moveToTail(node);
            return previousValue;
        }
        node = new Node<>(key, value);
        map.put(key, node);
        if(tail == null)
            head = tail = node;
        else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        if(removeEldestEntry(head))
            remove(head.key);
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        requireNonNull(m);
        if(!m.isEmpty())
            for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
                put(e.getKey(), e.getValue());
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

    public V remove(final Object key) {
        requireNonNull(key);
        Node<K, V> node = map.remove(key);
        if(node == null)
            return null;
        V value = node.value;
        if(node.prev != null)
            node.prev.next = node.next;
        else
            head = node.next;
        if(node.next != null)
            node.next.prev = node.prev;
        else
            tail = node.prev;
        node.prev = null;
        node.next = null;
        return value;
    }

    public boolean remove(final Object key, final Object value) {
        requireNonNull(key);
        requireNonNull(value);
        Node<K, V> node = map.get(key);
        if(node == null || !Objects.equals(node.value, value))
            return false;
        remove(key);
        return true;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }

    public V replace(final K key, final V value) {
        requireNonNull(key);
        requireNonNull(value);
        if(!this.key.isInstance(key))
            throw new ClassCastException();
        if(!this.value.isInstance(value))
            throw new ClassCastException();
        Node<K, V> node = map.get(key);
        if(node == null)
            return null;
        V old = node.value;
        node.value = value;
        return old;
    }

    public boolean replace(final K key, final V oldValue, final V newValue) {
        requireNonNull(key);
        requireNonNull(oldValue);
        requireNonNull(newValue);
        if(!this.key.isInstance(key))
            throw new ClassCastException();
        if(!this.value.isInstance(oldValue))
            throw new ClassCastException();
        if(!this.value.isInstance(newValue))
            throw new ClassCastException();
        Node<K, V> node = map.get(key);
        if(node == null)
            return false;
        if(node.value.equals(oldValue)) {
            node.setValue(newValue);
            return true;
        }
        return false;
    }

    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        Node<K, V> current = head;
        boolean first = true;
        while(current != null) {
            if(!first)
                stringBuilder.append(", ");
            stringBuilder.append(current);
            first = false;
            current = current.next;
        }
        return stringBuilder.append("}").toString();
    }

    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        if(isEmpty())
            return values;
        Node<K, V> current = head;
        while(current != null) {
            values.add(current.value);
            current = current.next;
        }
        return values;
    }

    private CustomLinkedHashMap(final Class<K> key, final Class<V> value, int maxEntries, float loadFactor, boolean accessOrder) {
        if(loadFactor <= 0 || loadFactor > 1 || maxEntries < 0)
            throw new IllegalArgumentException();
        this.key = key;
        this.value = value;
        this.maxEntries = maxEntries;
        this.accessOrder = accessOrder;
        int initialCapacity = (maxEntries == Integer.MAX_VALUE) ? 16 : (int) Math.ceil(maxEntries / loadFactor) + 1;
        this.map = new HashMap<>(initialCapacity, loadFactor);
    }

    private void moveToTail(Node<K, V> node) {
        if(node == tail)
            return;
        if(node.prev != null)
            node.prev.next = node.next;
        else
            head = node.next;
        if(node.next != null)
            node.next.prev = node.prev;
        node.prev = tail;
        node.next = null;
        if(tail == null)
            head = tail = node;
        else {
            tail.next = node;
            tail = node;
        }
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
            return key + "=" + value;
        }
    }
}
