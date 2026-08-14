package custommap;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
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
 * <p>This implementation permits both {@code null} keys and {@code null} values.
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

    private transient Set<K> cachedKeySet;
    private transient Set<Map.Entry<K, V>> cachedEntrySet;

    private transient Entry<K, V>[] table;
    private transient Entry<K, V> head;
    private transient Entry<K, V> tail;

    private transient int size;
    private transient int threshold;

    private transient Collection<V> cachedValues;

    private final boolean accessOrder;

    private final int maxEntries;

    private final float loadFactor;

    /**
     * Constructs an empty {@code CustomLinkedHashMap} with the default initial capacity (16),
     * load factor (0.75), and ordering mode (insertion-order).
     */
    public CustomLinkedHashMap() {
        this(16, 0.75f, false);
    }

    /**
     * Constructs an empty {@code CustomLinkedHashMap} with the specified initial capacity,
     * default load factor (0.75), and insertion-order mode.
     *
     * @param initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public CustomLinkedHashMap(final int initialCapacity) {
        this(initialCapacity, 0.75f, false);
    }

    /**
     * Constructs an empty {@code CustomLinkedHashMap} with the specified initial capacity
     * and load factor, operating in insertion-order mode.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     * or the load factor is non-positive or NaN
     */
    public CustomLinkedHashMap(final int initialCapacity, final float loadFactor) {
        this(initialCapacity, loadFactor, false);
    }

    /**
     * Constructs a new {@code CustomLinkedHashMap} with the same mappings as the specified map.
     * The map is created with an initial capacity sufficient to hold the mappings in the
     * specified map under a default load factor of 0.75, and operates in insertion-order mode.
     *
     * @param m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public CustomLinkedHashMap(final Map<? extends K, ? extends V> m) {
        requireNonNull(m);
        int initialCapacity = Math.max((int) (m.size() / 0.75f) + 1, 16);
        this.loadFactor = 0.75f;
        this.accessOrder = false;
        this.maxEntries = Integer.MAX_VALUE;
        int cap = 1;
        while (cap < initialCapacity)
            cap <<= 1;
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.threshold = (int) (cap * loadFactor);
        putAll(m);
    }

    public CustomLinkedHashMap(final int initialCapacity, final float loadFactor, final boolean accessOrder) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Initial capacity must not be negative: " + initialCapacity);
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        this.loadFactor = loadFactor;
        this.accessOrder = accessOrder;
        this.maxEntries = Integer.MAX_VALUE;
        int cap = 1;
        while (cap < initialCapacity)
            cap <<= 1;
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.threshold = (int) (cap * loadFactor);
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
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     */
    public boolean containsKey(final Object key) {
        Entry<K, V>[] tab = table;
        int h = hash(key);
        int index = (tab.length - 1) & h;
        for(Entry<K, V> entry = tab[index]; entry != null; entry = entry.next)
            if(entry.hash == h && (key == entry.key || key.equals(entry.key)))
                return true;
        return false;

    }

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
    public boolean containsValue(final Object value) {
        for(Entry<K, V> entry = head; entry != null; entry = entry.after) {
            V v = entry.value;
            if(v == value || (value != null && value.equals(v)))
                return true;
        }
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

                public void clear() {
                    CustomLinkedHashMap.this.clear();
                }

                public boolean contains(final Object o) {
                    if (!(o instanceof Map.Entry<?, ?> e))
                        return false;
                    Object key = e.getKey();
                    int h = hash(key);
                    Entry<K, V>[] tab = table;
                    if (tab == null || tab.length == 0)
                        return false;
                    int index = (tab.length - 1) & h;
                    for (Entry<K, V> candidate = tab[index]; candidate != null; candidate = candidate.next) {
                        if (candidate.hash == h) {
                            K k = candidate.key;
                            if (k == key || (key != null && key.equals(k))) {
                                return Objects.equals(candidate.value, e.getValue());
                            }
                        }
                    }
                    return false;
                }

                public Iterator<Map.Entry<K, V>> iterator() {
                    return new EntryIterator();
                }

                public boolean remove(final Object o) {
                    if(!(o instanceof Map.Entry<?, ?> e))
                        return false;
                    return CustomLinkedHashMap.this.remove(e.getKey(), e.getValue());
                }

                public int size() {
                    return size;
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
            if (!Objects.equals(entry.value, otherMap.get(entry.key)))
                return false;
        return true;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     * <p>
     * If an entry is found and this map was configured with {@code accessOrder = true},
     * the underlying structural node is moved to the tail of the doubly-linked list
     * to track recent access.
     *
     * @param key the key whose associated value is to be returned (can be null)
     * @return the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key
     */
    public V get(final Object key) {
        Entry<K, V>[] tab = table;
        if (tab == null || tab.length == 0)
            return null;
        int h = hash(key);
        int index = (tab.length - 1) & h;
        for(Entry<K, V> entry = tab[index]; entry != null; entry = entry.next)
            if (entry.hash == h) {
                K k = entry.key;
                if (k == key || (key != null && key.equals(k))) {
                    if (accessOrder)
                        moveToTail(entry);
                    return entry.value;
                }
            }
        return null;
    }

    /**
     * Returns the value to which the specified key is mapped, or the provided
     * {@code defaultValue} if this map contains no mapping for the key.
     * <p>
     * If a valid mapping is located and this map is configured for access-ordering,
     * the accessed node is relocated to the tail of the internal doubly-linked list.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the fallback value to return if the key is absent
     * @return the value to which the specified key is mapped, or {@code defaultValue}
     * if this map contains no mapping for the key
     */
    public V getOrDefault(final Object key, final V defaultValue) {
        Entry<K, V>[] tab = table;
        if (tab == null || tab.length == 0)
            return defaultValue;
        int h = hash(key);
        for (Entry<K, V> e = tab[(tab.length - 1) & h]; e != null; e = e.next)
            if (e.hash == h) {
                K k = e.key;
                if (k == key || (key != null && key.equals(k))) {
                    if (accessOrder)
                        moveToTail(e);
                    return e.value;
                }
            }
        return defaultValue;
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

                public boolean contains(final Object o) {
                    return containsKey(o);
                }

                public Iterator<K> iterator() {
                    return new KeyIterator();
                }

                public boolean remove(final Object o) {
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
     */
    public V put(final K key, final V value) {
        Entry<K, V>[] tab = table;
        if (size >= threshold) {
            resize(tab.length << 1);
            tab = table;
        }
        int h = hash(key);
        int index = (tab.length - 1) & h;
        for (Entry<K, V> entry = tab[index]; entry != null; entry = entry.next)
            if (entry.hash == h && (key == entry.key || key.equals(entry.key)))
                return updateExistingEntry(entry, value);
        addEntry(h, key, value, index);
        evictEldestIfNeeded();
        return null;
    }

    /**
     * Copies all the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for any of the
     * keys currently in the specified map.
     * <p>
     * If the source map contains elements, it iterates sequentially through its entry set,
     * delegating each individual key-value pair to the {@link #put(Object, Object)}
     * method. This ensures that structural linking and eviction checks are
     * consistently executed for every imported entry.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public void putAll(final Map<? extends K, ? extends V> m) {
        if(m == null)
            throw new NullPointerException();
        int numberElementsToBeAdded = m.size();
        if (numberElementsToBeAdded == 0)
            return;
        int targetCapacity = (int) Math.ceil((size + numberElementsToBeAdded) / loadFactor);
        if (targetCapacity > table.length) {
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            resize(newCapacity);
        }
        populate(m);
    }

    /**
     * Associates the specified value with the specified key if the key is not
     * already associated with a value.
     * <p>
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
     */
    public V putIfAbsent(final K key, final V value) {
        Entry<K, V>[] tab = table;
        if (size >= threshold) {
            resize(tab.length << 1);
            tab = table;
        }
        int h = hash(key);
        int index = (tab.length - 1) & h;
        for (Entry<K, V> e = tab[index]; e != null; e = e.next)
            if (e.hash == h && (key == e.key || key.equals(e.key)))
                return e.value;
        addEntry(h, key, value, index);
        evictEldestIfNeeded();
        return null;
    }

    /**
     * Replaces each entry's value with the result of invoking the given function on
     * that entry, in the order entries were currently returned by the map's iterator
     * (insertion or access order).
     * <p>
     * This method traverses the internal doubly-linked list, ensuring that all
     * value updates occur in the established iteration order.
     *
     * @param function the function to apply to each entry's key and value
     * @throws NullPointerException if the specified function is null,
     * or if the function computes a {@code null} replacement value
     */
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        requireNonNull(function);
        for(Entry<K, V> entry = head; entry != null; entry = entry.after)
            entry.value = requireNonNull(function.apply(entry.key, entry.value));
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * <p>
     * If a matching entry is located, it is removed from the backing lookup map and structurally
     * unlinked from the internal doubly-linked list.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
     */
    public V remove(final Object key) {
        int h = hash(key);
        Entry<K, V>[] tab = table;
        int index = (tab.length - 1) & h;
        Entry<K, V> previous = null;
        Entry<K, V> entry = tab[index];
        while(entry != null) {
            if(entry.hash == h && (key == entry.key || key.equals(entry.key))) {
                if(previous == null)
                    tab[index] = entry.next;
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
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed, {@code false} otherwise
     */
    public boolean remove(final Object key, final Object value) {
        int h = hash(key);
        Entry<K, V>[] tab = table;
        int index = (tab.length - 1) & h;
        Entry<K, V> previous = null;
        Entry<K, V> entry = table[index];
        while(entry != null) {
            if(entry.hash == h && (key == entry.key || key.equals(entry.key))) {
                if(!Objects.equals(value, entry.value))
                    return false;
                if(previous == null)
                    tab[index] = entry.next;
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
     * If a matching mapping is located in the map, its value is updated to the new specified value,
     * and the previous value is returned. If the key is not present, the map remains unmodified
     * and {@code null} is returned.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null}
     * if there was no mapping for the key
     */
    public V replace(final K key, final V value) {
        Entry<K, V>[] tab = table;
        int h = hash(key);
        int index = (tab.length - 1) & h;
        for (Entry<K, V> e = tab[index]; e != null; e = e.next)
            if (e.hash == h && (key == e.key || key.equals(e.key)))
                return updateExistingEntry(e, value);
        return null;
    }

    /**
     * Replaces the entry for the specified key only if it is currently mapped to
     * the specified old value.
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
     */
    public boolean replace(final K key, final V oldValue, final V newValue) {
        requireNonNull(key);
        requireNonNull(oldValue);
        requireNonNull(newValue);
        Entry<K, V>[] tab = table;
        int h = hash(key);
        int index = (tab.length - 1) & h;
        for(Entry<K, V> entry = tab[index]; entry != null; entry = entry.next)
            if (entry.hash == h && (key == entry.key || key.equals(entry.key))) {
                if (Objects.equals(entry.value, oldValue)) {
                    entry.value = newValue;
                    if (accessOrder)
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
     * This implementation returns the structural entry count in O(1) constant time.
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
                public void clear() {
                    CustomLinkedHashMap.this.clear();
                }

                public boolean contains(final Object o) {
                    return containsValue(o);
                }

                public Iterator<V> iterator() {
                    return new ValueIterator();
                }

                public int size() {
                    return size;
                }
            };
            cachedValues = valuesSet;
        }
        return valuesSet;
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
            this.value = newValue;
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

    private void addEntry(final int h, final K key, final V value, final int index) {
        Entry<K, V> newEntry = new Entry<>(h, key, value, table[index]);
        table[index] = newEntry;
        linkTail(newEntry);
        size++;
    }

    private void evictEldestIfNeeded() {
        if(head != null && removeEldestEntry(head))
            remove(head.key);
    }

    private static int hash(final Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
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

    private void populate(final Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            int h = hash(key);
            int index = (table.length - 1) & h;
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

    private void resize(final int newCapacity) {
        Entry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == 1 << 30) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> e = oldTable[i];
            if (e != null) {
                oldTable[i] = null;
                do {
                    Entry<K, V> next = e.next;
                    int index = (newCapacity - 1) & e.hash;
                    e.next = newTable[index];
                    newTable[index] = e;
                    e = next;
                } while (e != null);
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
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

    private V updateExistingEntry(final Entry<K, V> entry, final V value) {
        V oldValue = entry.value;
        entry.value = value;
        if (accessOrder)
            moveToTail(entry);
        return oldValue;
    }
}