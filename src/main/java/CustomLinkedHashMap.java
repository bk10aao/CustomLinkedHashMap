import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A hybrid hash table and doubly-linked list-based implementation of the {@link Map} interface,
 * utilizing an underlying {@link java.util.HashMap} for storage and lookups while maintaining
 * insertion or access order via an internal doubly-linked list.
 * <p>
 * This map prohibits {@code null} keys and strictly enforces runtime type constraints on both
 * keys and values using provided {@link Class} type tokens. Null values are permitted in some
 * operations (e.g., {@link #put(Object, Object)}) but forbidden in others (e.g., {@link #replace(Object, Object)}).
 * To prevent expensive rehashing, the initial capacity of the backing map is calculated dynamically at
 * construction based on the specified maximum entries and load factor.
 * <p>
 * An automated eviction policy is supported via {@link #removeEldestEntry(Map.Entry)}; when the map exceeds
 * its capacity threshold, the eldest entry at the head of the list is automatically evicted. Depending
 * on configuration, structural access operations can dynamically relocate nodes to the tail of the list.
 * <p>
 * <b>Note that this implementation is not synchronized</b> and does not detect concurrent modifications.
 * Structural modifications made during iteration over any collection views will produce undefined behavior.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Benjamin Kane
 * @see <a href="https://www.linkedin.com/in/benjamin-kane-81149482/">LinkedIn</a>
 * @see <a href="https://github.com/bk10aao">GitHub account bk10aao</a>
 * @see <a href="https://github.com/bk10aao/CustomLinkedHashMap">Repository</a>
 */
public class CustomLinkedHashMap<K, V> implements Map<K, V>, Serializable {

    private final int maxEntries;

    private final Map<K, Node<K, V>> map;

    private transient Node<K, V> head;
    private transient Node<K, V> tail;

    private final boolean accessOrder;

    private final Class<K> key;
    private final Class<V> value;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an empty insertion-ordered {@code CustomLinkedHashMap} instance
     * with a default initial capacity (16), default load factor (0.75),
     * and no maximum entry bounds.
     *
     * @param key the Class token for keys maintained by this map
     * @param value the Class token for mapped values
     * @throws NullPointerException if either the key or value Class token is null
     */
    public CustomLinkedHashMap(final Class<K> key, final Class<V> value) {
        this(key, value, Integer.MAX_VALUE, 0.75f, false);
    }

    /**
     * Constructs an empty insertion-ordered {@code CustomLinkedHashMap} instance
     * with the specified maximum entry capacity threshold, a default load factor (0.75),
     * and standard insertion-based ordering.
     *
     * @param key the Class token for keys maintained by this map
     * @param value the Class token for mapped values
     * @param maxEntries the maximum entry threshold before removal of the eldest entry triggers
     * @throws IllegalArgumentException if the maxEntries limit is negative
     */
    public CustomLinkedHashMap(final Class<K> key, final Class<V> value, int maxEntries) {
        this(key, value, maxEntries, 0.75f, false);
    }

    /**
     * Constructs an empty {@code CustomLinkedHashMap} instance with the specified
     * maximum entry capacity threshold, a default load factor (0.75), and the
     * designated iteration ordering mode.
     *
     * @param key the Class token for keys maintained by this map
     * @param value the Class token for mapped values
     * @param maxEntries the maximum entry threshold before removal of the eldest entry triggers
     * @param accessOrder the ordering mode - {@code true} for access-order, {@code false} for insertion-order
     * @throws IllegalArgumentException if the maxEntries limit is negative
     */
    public CustomLinkedHashMap(final Class<K> key, final Class<V> value, int maxEntries, final boolean accessOrder) {
        this(key, value, maxEntries, 0.75f, accessOrder);
    }

    /**
     * Constructs an empty insertion-ordered {@code CustomLinkedHashMap} instance
     * with no maximum entry bounds, standard insertion-based ordering, and the
     * specified performance load factor.
     *
     * @param key the Class token for keys maintained by this map
     * @param value the Class token for mapped values
     * @param loadFactor the load factor determining structural resize thresholds
     * @throws IllegalArgumentException if the load factor is non-positive or greater than 1
     */
    public CustomLinkedHashMap(final Class<K> key, final Class<V> value, float loadFactor) {
        this(key, value, Integer.MAX_VALUE, loadFactor, false);
    }

    /**
     * Removes all mappings from this map.
     * The map will be empty after this call returns, and both the head and tail
     * pointers of the internal doubly-linked list will be unlinked.
     */
    public void clear() {
        head = tail = null;
        map.clear();
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     * This implementation first performs a runtime type check against the established
     * key class token; if the specified key is not an instance of that class,
     * it immediately returns {@code false} instead of querying the map.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     */
    public boolean containsKey(final Object key) {
        if(!this.key.isInstance(key))
            return false;
        return map.containsKey(key);
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value.
     * This implementation first performs a runtime type check against the established
     * value class token; if the specified value is not an instance of that class,
     * it immediately returns {@code false}. Otherwise, it performs a linear traversal
     * from the head of the doubly-linked list to find a matching value.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
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

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are reflected in the set,
     * and vice-versa.
     * <p>
     * The view's iterator traverses the entries in the order dictated by the map's
     * configuration (either insertion-order or access-order), moving sequentially from
     * the current {@code head} node to the {@code tail}. The iterator also supports
     * safe element removal during iteration via {@link Iterator#remove()}.
     *
     * @return a set view of the mappings contained in this map
     */
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

    /**
     * Compares the specified object with this map for equality.
     * Returns {@code true} if the given object is also a map and the two maps
     * represent the same mappings, matching size and key-value pairings regardless
     * of iteration order.
     * <p>
     * If the object being compared is another {@code CustomLinkedHashMap}, this
     * implementation enforces an additional strict check requiring both maps to
     * maintain identical key and value {@link Class} type tokens before verifying
     * the underlying entries.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
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

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     * <p>
     * This implementation enforces that the search key is non-null. It then verifies
     * if the provided key is an instance of the configured key class token; if not,
     * it immediately returns {@code null}. If an entry is found and this map was
     * configured with {@code accessOrder = true}, the underlying structural node
     * is moved to the tail of the doubly-linked list to track recent access.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped,
     *          or {@code null} if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     */
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

    /**
     * Returns the value to which the specified key is mapped, or the provided
     * {@code defaultValue} if this map contains no mapping for the key.
     * <p>
     * This implementation enforces that the search key is non-null. It then checks
     * if the provided key is an instance of the configured key class token; if not,
     * it immediately returns the fallback {@code defaultValue}. If a valid mapping is
     * located and this map is configured for access-ordering, the accessed node is
     * relocated to the tail of the internal doubly-linked list.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the fallback value to return if the key is absent or type-incompatible
     * @return the value to which the specified key is mapped, or {@code defaultValue}
     *          if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     */
    public V getOrDefault(final Object key, final V defaultValue) {
        requireNonNull(key);
        if(!this.key.isInstance(key))
            return defaultValue;
        Node<K, V> node = map.get(key);
        if(node == null)
            return defaultValue;
        if(accessOrder)
            moveToTail(node);
        return node.getValue();
    }

    /**
     * Returns the hash code value for this map. The hash code of a map is defined
     * to be the sum of the hash codes of each entry in the map's {@code entrySet()}.
     * <p>
     * This implementation sequentially iterates through the internal doubly-linked
     * list via the entry set view, computing and summing the hash values of each
     * individual structural node to guarantee consistency with the contract defined
     * in {@link Map#hashCode}.
     *
     * @return the hash code value for this map
     * @see Map.Entry#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 0;
        for(Map.Entry<K, V> entry : entrySet())
            h += entry.hashCode();
        return h;
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     * <p>
     * This implementation checks whether the current tracking size of the
     * underlying map structure is equal to zero.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are reflected in the set,
     * and vice-versa.
     * <p>
     * The view's iterator traverses the keys in the order dictated by the map's
     * configuration (either insertion-order or access-order), moving sequentially from
     * the current {@code head} node to the {@code tail}. The iterator also supports
     * safe key removal and structural unlinking during iteration via {@link Iterator#remove()}.
     *
     * @return a set view of the keys contained in this map
     */
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

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     * <p>
     * This implementation strictly enforces that the inserted key cannot be null.
     * It performs explicit runtime type validation against both the key and value
     * class tokens, throwing a {@link ClassCastException} if a type mismatch is detected.
     * <p>
     * When an entry is updated, it may be moved to the tail of the list based on the
     * configured {@code accessOrder}. When a new entry is added, it is appended to
     * the tail of the internal doubly-linked list. Following an insertion, the
     * implementation checks {@link #removeEldestEntry(Map.Entry)}; if it returns
     * {@code true}, the oldest entry (located at the {@code head} of the list)
     * is structurally evicted from both the backing map and the sequential list.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
     * @throws IllegalArgumentException if the specified key is null
     * @throws ClassCastException if the key or value runtime types are incompatible
     * with this map's defined tokens
     */
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

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for any of the
     * keys currently in the specified map.
     * <p>
     * This implementation enforces that the source map is non-null. If the source
     * map contains elements, it iterates sequentially through its entry set,
     * delegating each individual key-value pair to the {@link #put(Object, Object)}
     * method. This ensures that type safety validation, structural linking, and
     * eviction checks are consistently executed for every imported entry.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     * @throws IllegalArgumentException if any key in the specified map is null
     * @throws ClassCastException if a key or value type in the specified map is incompatible with this map's defined tokens
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        requireNonNull(m);
        if(!m.isEmpty())
            for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
                put(e.getKey(), e.getValue());
    }

    /**
     * Associates the specified value with the specified key if the key is not
     * already associated with a value.
     * <p>
     * This implementation strictly enforces that both the input key and value are
     * non-null, throwing a {@link NullPointerException} if either parameter is missing.
     * If a valid mapping already exists for the key, its current value is immediately
     * returned without modifying the map. If no mapping exists, the key-value pair is
     * passed to {@link #put(Object, Object)}, where runtime type-token verification,
     * double-linked list positioning, and eldest-entry eviction are handled.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null}
     * if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     * @throws ClassCastException if the key or value runtime types are incompatible with this map's defined tokens
     */
    public V putIfAbsent(final K key, final V value) {
        requireNonNull(key);
        requireNonNull(value);
        Node<K, V> node = map.get(key);
        if(node != null)
            return node.value;
        put(key, value);
        return null;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * <p>
     * This implementation enforces that the search key is non-null. If a matching
     * entry is located, it is removed from the backing lookup map. The underlying
     * node is then structurally unlinked from the internal doubly-linked list by
     * dynamically adjusting neighboring pointers (updating {@code head} or {@code tail}
     * references where appropriate) and clearing the node's individual layout links
     * to prevent memory retention.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key is null
     */
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

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value.
     * <p>
     * This implementation strictly enforces that both the input key and value are
     * non-null, throwing a {@link NullPointerException} if either parameter is missing.
     * If the key exists in the map and its currently associated value matches the
     * provided value (tested via {@link Objects#equals(Object, Object)}), the entry is
     * passed to {@link #remove(Object)} to be structurally unlinked from both the
     * backing map and the doubly-linked list.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed, {@code false} otherwise
     * @throws NullPointerException if the specified key or value is null
     */
    public boolean remove(final Object key, final Object value) {
        requireNonNull(key);
        requireNonNull(value);
        Node<K, V> node = map.get(key);
        if(node == null || !Objects.equals(node.value, value))
            return false;
        remove(key);
        return true;
    }

    /**
     * Returns {@code true} if this map should remove its eldest entry.
     * <p>
     * This method is automatically invoked by {@link #put(Object, Object)} after
     * a new entry has been inserted into the map. It provides a hook to implement
     * an automated eviction policy (such as an LRU or FIFO cache). This specific
     * implementation returns {@code true} whenever the current size of the map
     * exceeds the configured {@code maxEntries} threshold, signaling that the
     * entry at the {@code head} of the doubly-linked list should be structurally removed.
     *
     * @param eldest the least recently accessed or oldest inserted entry in the map,
     * located at the head of the tracking list
     * @return {@code true} if the eldest entry should be removed from the map;
     * {@code false} if it should be retained
     */
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }

    /**
     * Replaces the entry for the specified key only if it is currently mapped to
     * some value.
     * <p>
     * This implementation strictly enforces that both the input key and value are
     * non-null, throwing a {@link NullPointerException} if either parameter is missing.
     * It also performs explicit runtime type validation against both the key and value
     * class tokens, throwing a {@link ClassCastException} if a type mismatch is detected.
     * If a matching mapping is located in the map, its value is updated to the new
     * specified value, and the previous value is returned. If the key is not present,
     * the map remains unmodified and {@code null} is returned.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null}
     * if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     * @throws ClassCastException if the key or value runtime types are incompatible
     * with this map's defined tokens
     */
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

    /**
     * Replaces the entry for the specified key only if it is currently mapped to
     * the specified old value.
     * <p>
     * This implementation strictly enforces that the input key, expected old value,
     * and new value are all non-null, throwing a {@link NullPointerException} if any
     * parameter is missing. It also executes explicit runtime type verification for
     * all three arguments against the map's established key and value class tokens,
     * throwing a {@link ClassCastException} upon a mismatch.
     * <p>
     * If the key exists in the map, its currently associated value is compared to
     * {@code oldValue} using {@link Object#equals(Object)}. The value is updated to
     * {@code newValue} and {@code true} is returned if and only if the values match.
     * Otherwise, the map remains unmodified and {@code false} is returned.
     *
     * @param key key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced, {@code false} otherwise
     * @throws NullPointerException if the specified key, oldValue, or newValue is null
     * @throws ClassCastException if the key, oldValue, or newValue runtime types
     * are incompatible with this map's defined tokens
     */
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

    /**
     * Returns the number of key-value mappings in this map.
     * <p>
     * This implementation delegates directly to the underlying backing map
     * to retrieve the structural entry count in $O(1)$ constant time.
     *
     * @return the number of key-value mappings in this map
     */
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

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * <p>
     * Unlike standard map implementations where the collection is backed by the map,
     * this implementation returns a snapshot copy stored in a new {@link ArrayList}.
     * If the map is empty, an empty list is immediately returned. Otherwise, the
     * method performs a sequential traversal from the {@code head} node to the
     * {@code tail} node, copying each entry's value into the collection in the
     * map's defined iteration order.
     *
     * @return a collection view of the values contained in this map
     */
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

    /**
     * Constructs a new, configured instance of {@code CustomLinkedHashMap}.
     * <p>
     * This private constructor initializes the structural properties of the map, including
     * the runtime type tokens for type-safety checks and the iteration ordering strategy. It
     * enforces strict bounds validations on both the load factor and maximum allowed entries.
     * <p>
     * To prevent performance-degrading resize and rehash operations during data insertion,
     * the initial capacity of the underlying backing {@link HashMap} is dynamically calculated
     * based on the provided {@code maxEntries} limit and {@code loadFactor}:
     * <p>
     * If {@code maxEntries} is unbounded ({@link Integer#MAX_VALUE}), a default capacity of 16 is applied.
     * Otherwise, the capacity is computed using the ceiling of the entries-to-load-factor ratio, adjusted
     * by an extra padding element:
     * <br>
     * {@code initialCapacity = ceil(maxEntries / loadFactor) + 1}
     *
     * @param key the {@link Class} token representing the allowed runtime type for keys
     * @param value the {@link Class} token representing the allowed runtime type for values
     * @param maxEntries the maximum number of elements this map can hold before triggering eviction
     * @param loadFactor the load factor threshold used to configure the backing map's resizing behavior
     * @param accessOrder {@code true} for access-order (least-recently accessed to most-recently accessed),
     *                    {@code false} for insertion-order
     * @throws IllegalArgumentException if {@code loadFactor <= 0}, {@code loadFactor > 1}, or {@code maxEntries < 0}
     */
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

    /**
     * A private, static internal node that encapsulates a single key-value mapping
     * and implements the {@link Map.Entry} interface.
     * <p>
     * In addition to storing the entry data, each node functions as a structural element
     * within the map's internal doubly-linked list. It maintains direct reference pointers
     * to its predecessor ({@code prev}) and successor ({@code next}) nodes, allowing for
     * efficient $O(1)$ structural ordering modifications, element linking, and sequential
     * data iteration.
     *
     * @param <K> the type of key maintained by this entry node
     * @param <V> the type of mapped value maintained by this entry node
     */
    private static class Node<K, V> implements Map.Entry<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        /**
         * Constructs a new entry node with the specified key and value.
         * The structural pointer references ({@code prev} and {@code next})
         * are implicitly initialized to {@code null}.
         *
         * @param key the key representing this mapping
         * @param value the value associated with the key
         */
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the key corresponding to this entry.
         *
         * @return the key corresponding to this entry
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Returns the value corresponding to this entry.
         *
         * @return the value corresponding to this entry
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value corresponding to this entry with the specified value.
         *
         * @param newValue new value to be stored in this entry
         * @return the old value corresponding to the entry
         */
        @Override
        public V setValue(V newValue) {
            V old = value;
            value = newValue;
            return old;
        }

        /**
         * Returns a string representation of this entry in the format {@code key=value}.
         *
         * @return a string representation of this entry mapping
         */
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
