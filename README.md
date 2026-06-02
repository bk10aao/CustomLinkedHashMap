# CustomLinkedHashMap

Implementation of a LinkedHashMap.

All methods implemented are identical to those found in the Java Map interface.

# Build and Test

To build and test the project run command `./gradlew clean build`

To test the project run command `./gradlew test`

# Time Complexity

| Method                             | CustomLinkedHashMap              | Java LinkedHashMap               | Winner  |
|:-----------------------------------|:---------------------------------|:---------------------------------|:--------|
| **`clear()`**                      | $O(1)$                           | $O(1)$                           | **Tie** |
| **`containsKey(Object)`**          | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`containsValue(Object)`**        | $O(n)$                           | $O(n)$                           | **Tie** |
| **`entrySet()`**                   | $O(1)$ (view creation)           | $O(1)$ (view creation)           | **Tie** |
| **`equals(Object)`**               | $O(n)$                           | $O(n)$                           | **Tie** |
| **`get(Object)`**                  | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`getOrDefault(Object, V)`**      | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`hashCode()`**                   | $O(n)$                           | $O(n)$                           | **Tie** |
| **`isEmpty()`**                    | $O(1)$                           | $O(1)$                           | **Tie** |
| **`keySet()`**                     | $O(1)$ (view creation)           | $O(1)$ (view creation)           | **Tie** |
| **`put(K, V)`**                    | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`putAll(Map)`**                  | $O(m)$ avg, $O(m \cdot n)$ worst | $O(m)$ avg, $O(m \cdot n)$ worst | **Tie** |
| **`putIfAbsent(K, V)`**            | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`remove(Object)`**               | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`remove(Object, Object)`**       | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`removeEldestEntry(Map.Entry)`** | $O(1)$                           | $O(1)$                           | **Tie** |
| **`replace(K, V)`**                | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`replace(K, V, V)`**             | $O(1)$ avg, $O(n)$ worst         | $O(1)$ avg, $O(n)$ worst         | **Tie** |
| **`size()`**                       | $O(1)$                           | $O(1)$                           | **Tie** |
| **`toString()`**                   | $O(n)$                           | $O(n)$                           | **Tie** |
| **`values()`**                     | $O(1)$ *(Using view)*            | $O(1)$                           | **Tie** |

# Space Complexity

| Operation / Method                 | CustomLinkedHashMap   | Java LinkedHashMap | Winner  |
|:-----------------------------------|:----------------------|:-------------------|:--------|
| **Base Storage State**             | $O(n)$                | $O(n)$             | **Tie** |
| **`clear()`**                      | $O(1)$                | $O(1)$             | **Tie** |
| **`containsKey(Object)`**          | $O(1)$                | $O(1)$             | **Tie** |
| **`containsValue(Object)`**        | $O(1)$                | $O(1)$             | **Tie** |
| **`entrySet()`**                   | $O(1)$                | $O(1)$             | **Tie** |
| **`equals(Object)`**               | $O(1)$                | $O(1)$             | **Tie** |
| **`get(Object)`**                  | $O(1)$                | $O(1)$             | **Tie** |
| **`getOrDefault(Object, V)`**      | $O(1)$                | $O(1)$             | **Tie** |
| **`hashCode()`**                   | $O(1)$                | $O(1)$             | **Tie** |
| **`isEmpty()`**                    | $O(1)$                | $O(1)$             | **Tie** |
| **`keySet()`**                     | $O(1)$                | $O(1)$             | **Tie** |
| **`put(K, V)`**                    | $O(1)$ auxiliary      | $O(1)$ auxiliary   | **Tie** |
| **`putAll(Map)`**                  | $O(1)$ auxiliary      | $O(1)$ auxiliary   | **Tie** |
| **`putIfAbsent(K, V)`**            | $O(1)$ auxiliary      | $O(1)$ auxiliary   | **Tie** |
| **`remove(Object)`**               | $O(1)$                | $O(1)$             | **Tie** |
| **`remove(Object, Object)`**       | $O(1)$                | $O(1)$             | **Tie** |
| **`removeEldestEntry(Map.Entry)`** | $O(1)$                | $O(1)$             | **Tie** |
| **`replace(K, V)`**                | $O(1)$                | $O(1)$             | **Tie** |
| **`replace(K, V, V)`**             | $O(1)$                | $O(1)$             | **Tie** |
| **`size()`**                       | $O(1)$                | $O(1)$             | **Tie** |
| **`toString()`**                   | $O(n)$ auxiliary      | $O(n)$ auxiliary   | **Tie** |
| **`values()`**                     | $O(1)$ *(Using view)* | $O(1)$             | **Tie** |

**Notes**:
- **m**: Number of buckets in the map.
- **n**: Number of key-value mappings.