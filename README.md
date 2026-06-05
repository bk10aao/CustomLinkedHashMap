# Custom Linked HashMap

Implementation of a LinkedHashMap.

All methods implemented are identical to those found in the Java Map interface.

# Build and Test

To build and test the project run command `./gradlew clean build`

To test the project run command `./gradlew test`

# Time Complexity

| Method                       | CustomLinkedHashMap                           | LinkedHashMap (JDK)                     | Winner |
|------------------------------|-----------------------------------------------|-----------------------------------------|--------|
| **clear()**                  | O(n)                                          | O(n)                                    | Tie    |
| **containsKey(Object)**      | O(1) average                                  | O(1) average                            | Tie    |
| **containsValue(Object)**    | O(n)                                          | O(n)                                    | Tie    |
| **entrySet()**               | O(1) view creation, O(n) iteration            | O(1) view creation, O(n) iteration      | Tie    |
| **equals(Object)**           | O(n)                                          | O(n)                                    | Tie    |
| **get(Object)**              | O(1) average (O(1) moveToTail if accessOrder) | O(1) average (O(1) move if accessOrder) | Tie    |
| **getOrDefault(Object, V)**  | O(1) average (O(1) moveToTail if accessOrder) | O(1) average                            | Tie    |
| **hashCode()**               | O(n)                                          | O(n)                                    | Tie    |
| **isEmpty()**                | O(1)                                          | O(1)                                    | Tie    |
| **keySet()**                 | O(1) view creation, O(n) iteration            | O(1) view creation, O(n) iteration      | Tie    |
| **put(K, V)**                | O(1) average (O(1) eviction possible)         | O(1) average                            | Tie    |
| **putAll(Map)**              | O(m) average (m = input size)                 | O(m) average                            | Tie    |
| **putIfAbsent(K, V)**        | O(1) average (O(1) eviction possible)         | O(1) average                            | Tie    |
| **remove(Object)**           | O(1) average                                  | O(1) average                            | Tie    |
| **remove(Object, Object)**   | O(1) average                                  | O(1) average                            | Tie    |
| **removeEldestEntry(Entry)** | O(1)                                          | O(1)                                    | Tie    |
| **replace(K, V)**            | O(1) average (O(1) moveToTail if accessOrder) | O(1) average                            | Tie    |
| **replace(K, V, V)**         | O(1) average (O(1) moveToTail if accessOrder) | O(1) average                            | Tie    |
| **size()**                   | O(1)                                          | O(1)                                    | Tie    |
| **toString()**               | O(n)                                          | O(n)                                    | Tie    |
| **values()**                 | O(1) view creation, O(n) iteration            | O(1) view creation, O(n) iteration      | Tie    |

# Space Complexity

| Method / Aspect              | CustomLinkedHashMap             | LinkedHashMap (JDK)   | Winner |
|------------------------------|---------------------------------|-----------------------|--------|
| **clear()**                  | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **containsKey(Object)**      | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **containsValue(Object)**    | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **entrySet()**               | O(1) auxiliary (view)           | O(1) auxiliary (view) | Tie    |
| **equals(Object)**           | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **get(Object)**              | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **getOrDefault(Object, V)**  | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **hashCode()**               | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **isEmpty()**                | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **keySet()**                 | O(1) auxiliary (view)           | O(1) auxiliary (view) | Tie    |
| **put(K, V)**                | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **putAll(Map)**              | O(m) auxiliary (m = input size) | O(m) auxiliary        | Tie    |
| **putIfAbsent(K, V)**        | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **remove(Object)**           | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **remove(Object, Object)**   | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **removeEldestEntry(Entry)** | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **replace(K, V)**            | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **replace(K, V, V)**         | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **size()**                   | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **toString()**               | O(1) auxiliary                  | O(1) auxiliary        | Tie    |
| **values()**                 | O(1) auxiliary (view)           | O(1) auxiliary (view) | Tie    |

**Notes**:
- **n**: Number of key-value mappings (entries) currently in the map.
- **m**: Number of buckets (slots) in the underlying hash table.