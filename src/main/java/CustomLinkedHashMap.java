import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

/**
 * A specialized hash table and doubly-linked list-based implementation of the {@link Map} interface.
 * <p>Unlike standard map implementations, this class manages its own hash table array and
 * collision resolution chain, avoiding dependency on {@code java.util.HashMap}. It maintains
 * insertion or access order via an internal doubly-linked list of {@link Entry} objects.
 * <p>This implementation prohibits both {@code null} keys and {@code null} values, enforcing
 * runtime type safety via {@link Class} type tokens provided at construction.
 * <p>The map uses a power-of-two table size for efficient bitwise index masking and maintains
 * a load factor-based threshold to trigger rehashing. Structural access order is optionally
 * maintained by relocating accessed nodes to the tail of the list.
 * <p><b>Note:</b> This implementation is not synchronized.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Benjamin Kane
 * @see <a href="https://www.linkedin.com/in/benjamin-kane-81149482/">LinkedIn</a>
 * @see <a href="https://github.com/bk10aao">GitHub account bk10aao</a>
 * @see <a href="https://github.com/bk10aao/CustomLinkedHashMap">Repository</a>
 */
public class CustomLinkedHashMap<K, V> implements Map<K, V> {

    private final Class<K> keyType;
    private final Class<V> valueType;

    private transient Set<K> cachedKeySet;
    private transient Set<Map.Entry<K, V>> cachedEntrySet;

    private transient Collection<V> cachedValues;

    private final boolean accessOrder;

    private final int maxEntries;

    private transient int size;
    private transient int threshold;

    private final float loadFactor = 0.75f;

    private transient Entry<K, V>[] table;
    private transient Entry<K, V> head;
    private transient Entry<K, V> tail;

    public CustomLinkedHashMap(final Class<K> keyType, final Class<V> valueType, final int maxEntries, final boolean accessOrder) {
        this.keyType = requireNonNull(keyType);
        this.valueType = requireNonNull(valueType);
        if(maxEntries <= 0)
            throw new IllegalArgumentException("Maximum entries must be greater than zero.");
        this.maxEntries = maxEntries;
        this.accessOrder = accessOrder;
        int capacity = (maxEntries == Integer.MAX_VALUE) ? 16 : Math.max(16, (int) Math.ceil(maxEntries / loadFactor));
        int cap = 1;
        while(cap < capacity)
            cap <<= 1;
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.threshold = (int) (cap * loadFactor);
    }

    /**
     * Constructs an empty insertion-ordered {@code CustomLinkedHashMap} instance
     * with a default initial capacity (16), default load factor (0.75),
     * and no maximum entry bounds.
     *
     * @param keyType the Class token for keys maintained by this map
     * @param valueType the Class token for mapped values
     * @throws NullPointerException if either the key or value Class token is null
     */
    public CustomLinkedHashMap(final Class<K> keyType, final Class<V> valueType) {
        this(keyType, valueType, Integer.MAX_VALUE, false);
    }

    /**
     * Constructs an empty insertion-ordered {@code CustomLinkedHashMap} instance
     * with the specified maximum entry capacity threshold, a default load factor (0.75),
     * and standard insertion-based ordering.
     *
     * @param keyType the Class token for keys maintained by this map
     * @param valueType the Class token for mapped values
     * @param maxEntries the maximum entry threshold before removal of the eldest entry triggers
     * @throws IllegalArgumentException if the maxEntries limit is negative
     */
    public CustomLinkedHashMap(final Class<K> keyType, final Class<V> valueType, final int maxEntries) {
        this(keyType, valueType, maxEntries, 0.75f, false);
    }

    /**
     * Removes all mappings from this map.
     * The map will be empty after this call returns, and both the head and tail
     * pointers of the internal doubly-linked list will be unlinked.
     */
    public void clear() {
        if(size == 0)
            return;
        size = 0;
        Arrays.fill(table, null);
        head = null;
        tail = null;
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
        if(key == null)
            return false;
        int h = hash(key);
        for(Entry<K, V> entry = table[getIndex(h)]; entry != null; entry = entry.next)
            if(entry.hash == h && Objects.equals(key, entry.key))
                return true;
        return false;
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
        if(value == null)
            return false;
        for(Entry<K, V> entry = head; entry != null; entry = entry.after)
            if(Objects.equals(value, entry.value))
                return true;
        return false;
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are reflected in the set,
     * and vice versa.
     * <p>
     * The view's iterator traverses the entries in the order dictated by the map's
     * configuration (either insertion-order or access-order), moving sequentially from
     * the current {@code head} node to the {@code tail}. The iterator also supports
     * safe element removal during iteration via {@link Iterator#remove()}.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = cachedEntrySet;
        if(entrySet == null) {
            entrySet = new AbstractSet<>() {
                public int size() {
                    return size;
                }

                public void clear() {
                    CustomLinkedHashMap.this.clear();
                }

                public boolean contains(final Object o) {
                    if(!(o instanceof Map.Entry<?, ?> e))
                        return false;
                    V val = get(e.getKey());
                    return val != null && Objects.equals(val, e.getValue());
                }

                public boolean remove(final Object o) {
                    if(!(o instanceof Map.Entry<?, ?> e))
                        return false;
                    return CustomLinkedHashMap.this.remove(e.getKey(), e.getValue());
                }

                public Iterator<Map.Entry<K, V>> iterator() {
                    return new EntryIterator();
                }
            };
            cachedEntrySet = entrySet;
        }
        return entrySet;
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
        for(Entry<K, V> entry = head; entry != null; entry = entry.after)
            if(!entry.value.equals(otherMap.get(entry.key)))
                return false;
        return true;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     * <p>
     * If the provided key is null or not an instance of the configured key class token,
     * this implementation gracefully returns {@code null} to guarantee compatibility
     * with symmetric mixed map collections validations. If an entry is found and this
     * map was configured with {@code accessOrder = true}, the underlying structural node
     * is moved to the tail of the doubly-linked list to track recent access.
     *
     * @param key the key whose associated value is to be returned (can be null)
     * @return the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key
     */
    public V get(final Object key) {
        if(key == null)
            return null;
        int h = hash(key);
        for(Entry<K, V> entry = table[getIndex(h)]; entry != null; entry = entry.next)
            if(entry.hash == h && Objects.equals(key, entry.key)) {
                if(accessOrder)
                    moveToTail(entry);
                return entry.value;
            }
        return null;
    }

    /**
     * Returns the value to which the specified key is mapped, or the provided
     * {@code defaultValue} if this map contains no mapping for the key.
     * <p>
     * If the provided key is not an instance of the configured key class token,
     * this implementation gracefully returns the fallback {@code defaultValue}. If a valid
     * mapping is located and this map is configured for access-ordering, the accessed node is
     * relocated to the tail of the internal doubly-linked list.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the fallback value to return if the key is absent or type-incompatible
     * @return the value to which the specified key is mapped, or {@code defaultValue}
     * if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     */
    public V getOrDefault(final Object key, final V defaultValue) {
        V value = get(key);
        return (value != null || containsKey(key)) ? value : defaultValue;
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
        for(Entry<K, V> entry = head; entry != null; entry = entry.after)
            h += entry.hashCode();
        return h;
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are reflected in the set,
     * and vice versa.
     * <p>
     * The view's iterator traverses the keys in the order dictated by the map's
     * configuration (either insertion-order or access-order), moving sequentially from
     * the current {@code head} node to the {@code tail}. The iterator also supports
     * safe key removal and structural unlinking during iteration via {@link Iterator#remove()}.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<K> keySet() {
        Set<K> keySet = cachedKeySet;
        if(keySet == null) {
            keySet = new AbstractSet<>() {
                public void clear() {
                    CustomLinkedHashMap.this.clear();
                }

                public boolean contains(Object o) {
                    return containsKey(o);
                }

                public Iterator<K> iterator() {
                    return new KeyIterator();
                }

                public boolean remove(Object o) {
                    return CustomLinkedHashMap.this.remove(o) != null;
                }

                public int size() {
                    return size;
                }
            };
            cachedKeySet = keySet;
        }
        return keySet;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     * <p>
     * This implementation strictly enforces that the inserted key and value cannot be null,
     * throwing a {@link NullPointerException} if either parameter is missing.
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
     * @throws NullPointerException if the specified key or value is null
     * @throws ClassCastException if the key or value runtime types are incompatible
     * with this map's defined tokens
     */
    public V put(final K key, final V value) {
        return putVal(hash(key), key, value);
    }

    /**
     * Copies all the mappings from the specified map to this map.
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
     * @throws NullPointerException if the specified map is null, or if any key or value in the specified map is null
     * @throws IllegalArgumentException if any key in the specified map is null
     * @throws ClassCastException if a key or value type in the specified map is incompatible with this map's defined tokens
     */
    public void putAll(final Map<? extends K, ? extends V> m) {
        int numberElementsToBeAdded = m.size();
        if (numberElementsToBeAdded == 0)
            return;
        int targetCapacity = (int) ((size + numberElementsToBeAdded) / loadFactor) + 1;
        while (table.length < targetCapacity)
            resize();
        populate(m);
    }

    /**
     * Replaces each entry's value with the result of invoking the given function on
     * that entry, in the order entries were currently returned by the map's iterator
     * (insertion or access order).
     * <p>
     * This method traverses the internal doubly-linked list, ensuring that all
     * value updates occur in the established iteration order. The provided function
     * is applied to every key-value pair, and the returned value is validated
     * to ensure it is non-null.
     *
     * @param function the function to apply to each entry's key and value
     * @throws NullPointerException if the specified function is null or if the
     * function results in a null value
     */
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        requireNonNull(function);
        for(Entry<K, V> entry = head; entry != null; entry = entry.after)
            entry.value = requireNonNull(function.apply(entry.key, entry.value));
    }

    /**
     * Associates the specified value with the specified key if the key is not
     * already associated with a value.
     * <p>
     * This implementation strictly enforces that both the input key and value are
     * non-null, throwing a {@link NullPointerException} if either parameter is missing.
     * If a valid mapping already exists for the key, its current value is immediately
     * returned without modifying the map. If no mapping exists, the node is inserted
     * directly into the backing map and tracked at the tail of the iteration order list.
     * If inserting this element triggers the map's capacity policy eviction check, the
     * eldest entry at the head of the list is cleanly removed in constant time.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null}
     * if there was no mapping for the key
     * @throws ClassCastException if the key or value runtime types are incompatible
     * with this map's defined tokens
     */
    public V putIfAbsent(final K key, final V value) {
        validateTypes(key, value);
        int h = hash(key);
        int index = getIndex(h);
        for (Entry<K, V> e = table[index]; e != null; e = e.next)
            if (e.hash == h && key.equals(e.key))
                return e.value;
        addEntry(h, key, value, index);
        if (size > threshold)
            resize();
        evictEldestIfNeeded();
        return null;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * <p>
     * This implementation enforces that the search key is non-null, throwing a
     * {@link NullPointerException} if it is missing. It also verifies that the type of the key
     * matches the configured key class token, throwing a {@link ClassCastException} upon mismatch.
     * If a matching entry is located, it is removed from the backing lookup map and structurally
     * unlinked from the internal doubly-linked list.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key is null
     * @throws ClassCastException if the key type is incompatible with this map's defined tokens
     */
    public V remove(final Object key) {
        requireNonNull(key);
        if (!keyType.isInstance(key))
            throw new ClassCastException();
        int h = hash(key);
        int index = getIndex(h);
        Entry<K, V> previous = null;
        Entry<K, V> entry = table[index];
        while(entry != null) {
            if(entry.hash == h && (key == entry.key || key.equals(entry.key))) {
                if(previous == null)
                    table[index] = entry.next;
                else
                    previous.next = entry.next;
                unlink(entry);
                size--;
                return entry.value;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value.
     * <p>
     * This implementation verifies parameters leniently; if either the key or value is
     * {@code null}, or if their runtime classes are incompatible with this map's defined tokens,
     * it immediately returns {@code false} without modifying the map or throwing an exception.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed, {@code false} otherwise
     */
    public boolean remove(final Object key, final Object value) {
        if(key == null || value == null)
            return false;
        if (!keyType.isInstance(key) || !valueType.isInstance(value))
            return false;
        int h = hash(key);
        int index = getIndex(h);
        Entry<K, V> previous = null;
        Entry<K, V> entry = table[index];
        while(entry != null) {
            if(entry.hash == h && Objects.equals(key, entry.key)) {
                if(!Objects.equals(value, entry.value))
                    return false;
                if(previous == null)
                    table[index] = entry.next;
                else
                    previous.next = entry.next;
                unlink(entry);
                size--;
                return true;
            }
            previous = entry;
            entry = entry.next;
        }
        return false;
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
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }

    /**
     * Replaces the entry for the specified key only if it is currently mapped to
     * some value.
     * <p>
     * This implementation performs explicit runtime type validation against both the key and value
     * class tokens using {@link Class#isInstance(Object)}, throwing a {@link ClassCastException}
     * if a type mismatch is detected. Because a {@code null} reference is not an instance of any
     * class, passing a {@code null} key or value will also result in a {@link ClassCastException}.
     * If a matching mapping is located in the map, its value is updated to the new specified value,
     * and the previous value is returned. If the key is not present, the map remains unmodified
     * and {@code null} is returned.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null}
     * if there was no mapping for the key
     * @throws ClassCastException if the key or value runtime types are incompatible with this map's defined tokens,
     * or if either parameter is null with this map's defined tokens, or if either parameter is null
     */
    public V replace(final K key, final V value) {
        requireNonNull(key);
        requireNonNull(value);
        validateTypes(key, value);
        int h = hash(key);
        for(Entry<K, V> entry = table[getIndex(h)]; entry != null; entry = entry.next)
            if(entry.hash == h && Objects.equals(key, entry.key))
                return updateExistingEntry(entry, value);
        return null;
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
        if(!this.valueType.isInstance(oldValue))
            throw new ClassCastException();
        validateTypes(key, newValue);
        int h = hash(key);
        for(Entry<K, V> entry = table[getIndex(h)]; entry != null; entry = entry.next)
            if(entry.hash == h && Objects.equals(key, entry.key)) {
                if(entry.value.equals(oldValue)) {
                    entry.value = newValue;
                    if(accessOrder)
                        moveToTail(entry);
                    return true;
                }
                return false;
            }
        return false;
    }

    /**
     * Returns the number of key-value mappings in this map.
     * <p>
     * This implementation delegates directly to the underlying backing map
     * to retrieve the structural entry count in O(1) constant time.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * Returns a string representation of this map layout containing comma-separated
     * entries wrapped inside curly braces, following the sequential order
     * dictated by the internal doubly-linked list.
     *
     * @return a string representation of this map structure
     */
    @Override
    public String toString() {
        if(size == 0)
            return "{}";
        StringBuilder stringBuilder = new StringBuilder(size() * 16 + 2).append('{');
        for(Entry<K, V> entry = head; entry != null; entry = entry.after) {
            stringBuilder.append(entry.key).append("=").append(entry.value);
            if(entry.after != null)
                stringBuilder.append(", ");
        }
        return stringBuilder.append("}").toString();
    }

    /**
     * Returns a dynamic {@link Collection} view of the values contained in this map.
     * <p>
     * The collection is backed directly by the map layout, so additions, deletions,
     * and sequence mutations made to the backing map will instantly update the visibility
     * boundaries of this view layer.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values() {
        Collection<V> valuesSet = cachedValues;
        if(valuesSet == null) {
            valuesSet = new AbstractCollection<>() {
                public int size() {
                    return size;
                }

                public void clear() {
                    CustomLinkedHashMap.this.clear();
                }

                public boolean contains(final Object o) {
                    return containsValue(o);
                }

                public Iterator<V> iterator() {
                    return new ValueIterator();
                }
            };
            cachedValues = valuesSet;
        }
        return valuesSet;
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
     * @param keyType the {@link Class} token representing the allowed runtime type for keys
     * @param valueType the {@link Class} token representing the allowed runtime type for values
     * @param maxEntries the maximum number of elements this map can hold before triggering eviction
     * @param loadFactor the load factor threshold used to configure the backing map's resizing behavior
     * @param accessOrder {@code true} for access-order (least-recently accessed to most-recently accessed),
     *                    {@code false} for insertion-order
     * @throws IllegalArgumentException if {@code maxEntries < 0}
     */
    private CustomLinkedHashMap(final Class<K> keyType, final Class<V> valueType, final int maxEntries, final float loadFactor, final boolean accessOrder) {
        this.keyType = requireNonNull(keyType);
        this.valueType = requireNonNull(valueType);
        if(maxEntries <= 0)
            throw new IllegalArgumentException();
        this.maxEntries = maxEntries;
        this.accessOrder = accessOrder;
        int capacity = (maxEntries == Integer.MAX_VALUE) ? 16 : Math.max(16, (int)Math.ceil(maxEntries / loadFactor));
        int cap = 1;
        while(cap < capacity)
            cap <<= 1;
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.threshold = (int)(cap * loadFactor);
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * A private, static internal node that encapsulates a single key-value mapping
     * and implements the {@link Map.Entry} interface.
     * <p>
     * In addition to storing the entry data, each node functions as a structural element
     * within the map's internal doubly-linked list. It maintains direct reference pointers
     * to its predecessor ({@code before}) and successor ({@code after}) nodes, allowing for
     * efficient $O(1)$ structural ordering modifications, element linking, and sequential
     * data iteration.
     *
     * @param <K> the type of key maintained by this entry node
     * @param <V> the type of mapped value maintained by this entry node
     */
    private static class Entry<K, V> implements Map.Entry<K, V> {
        private final int hash;
        private final K key;
        private V value;

        private Entry<K, V> next;
        private Entry<K, V> before;
        private Entry<K, V> after;

        public Entry(final int hash, final K key, final V value, Entry<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public boolean equals(final Object o) {
            if(o == this)
                return true;
            if(o instanceof Map.Entry<?,?> e)
                return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
            return false;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public V setValue(final V newValue) {
            V old = value;
            this.value = requireNonNull(newValue);
            return old;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private abstract class LinkedHashIterator {
        private Entry<K, V> next = head;
        private Entry<K, V> lastReturned = null;

        public final boolean hasNext() {
            return next != null;
        }

        public final Entry<K, V> nextNode() {
            Entry<K, V> e = next;
            if(e == null)
                throw new NoSuchElementException();
            lastReturned = e;
            next = e.after;
            return e;
        }

        public final void remove() {
            if(lastReturned == null)
                throw new IllegalStateException();
            CustomLinkedHashMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }

    private final class KeyIterator extends LinkedHashIterator implements Iterator<K> {
        public K next() {
            return nextNode().getKey();
        }
    }

    private final class ValueIterator extends LinkedHashIterator implements Iterator<V> {
        public V next() {
            return nextNode().value;
        }
    }

    private final class EntryIterator extends LinkedHashIterator implements Iterator<Map.Entry<K, V>> {
        public Map.Entry<K, V> next() {
            return nextNode();
        }
    }

    private void addEntry(final int h, final Object key, final Object value, final int index) {
        Entry<K, V> newEntry = new Entry<>(h, (K) key, (V) value, table[index]);
        table[index] = newEntry;
        linkTail(newEntry);
        size++;
    }

    private void evictEldestIfNeeded() {
        if(head != null && removeEldestEntry(head))
            remove(head.key);
    }

    private int getIndex(final int h) {
        return (table.length - 1) & h;
    }

    private static int hash(final Object key) {
        int h;
        return (h = key.hashCode()) ^ (h >>> 16);
    }

    private void linkTail(final Entry<K,V> entry) {
        if(head == null)
            head = entry;
        else {
            tail.after = entry;
            entry.before = tail;
        }
        tail = entry;
    }

    private void moveToTail(final Entry<K, V> node) {
        if(node == tail)
            return;
        if(node.before != null)
            node.before.after = node.after;
        else
            head = node.after;
        if(node.after != null)
            node.after.before = node.before;
        node.before = tail;
        node.after = null;
        tail.after = node;
        tail = node;
    }

    private void populate(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            validateTypes(key, value);
            int h = hash(key);
            int index = getIndex(h);
            boolean exists = false;
            for (Entry<K, V> e = table[index]; e != null; e = e.next)
                if (e.hash == h && (key == e.key || key.equals(e.key))) {
                    updateExistingEntry(e, value);
                    exists = true;
                    break;
                }
            if (!exists) {
                addEntry(h, key, value, index);
                evictEldestIfNeeded();
            }
        }
    }

    private V putVal(final int h, final K key, final V value) {
        validateTypes(key, value);
        int index = getIndex(h);
        for (Entry<K, V> entry = table[index]; entry != null; entry = entry.next)
            if (entry.hash == h && (key == entry.key || key.equals(entry.key)))
                return updateExistingEntry(entry, value);
        addEntry(h, key, value, index);
        if (size > threshold)
            resize();
        evictEldestIfNeeded();
        return null;
    }

    private void resize() {
        int newCapacity = table.length << 1;
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
        for(Entry<K, V> entry = head; entry != null; entry = entry.after) {
            int index = (newCapacity - 1) & entry.hash;
            entry.next = newTable[index];
            newTable[index] = entry;
        }
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    private void unlink(final Entry<K, V> entry) {
        if(entry.before != null)
            entry.before.after = entry.after;
        else
            head = entry.after;
        if(entry.after != null)
            entry.after.before = entry.before;
        else
            tail = entry.before;
        entry.before = null;
        entry.after = null;
    }

    private V updateExistingEntry(final Entry<K, V> entry, final Object value) {
        V oldValue = entry.value;
        entry.value = (V) value;
        if (accessOrder)
            moveToTail(entry);
        return oldValue;
    }

    private void validateTypes(final K key, final V value) {
        if(!this.keyType.isInstance(key) || !this.valueType.isInstance(value))
            throw new ClassCastException();
    }
}