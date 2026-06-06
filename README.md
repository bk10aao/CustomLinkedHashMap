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

| Method              | Custom Avg (ns)      | Native Avg (ns)      | Multiplier | Faster Implementation |
|:--------------------|:---------------------|:---------------------|:-----------|:----------------------|
| `clear()`           | $1.23 \times 10^{5}$ | $1.31 \times 10^{5}$ | **x0.9**   | CustomLinkedHashMap   |
| `containsKey(K)`    | $1.67 \times 10^{6}$ | $1.24 \times 10^{6}$ | **x1.3**   | Standard Library      |
| `containsValue(V)`  | $4.87 \times 10^{9}$ | $4.82 \times 10^{9}$ | **x1.0**   | Standard Library      |
| `entrySet()`        | $9.92 \times 10^{2}$ | $1.00 \times 10^{3}$ | **x1.0**   | CustomLinkedHashMap   |
| `equals(Object o)`  | $1.36 \times 10^{6}$ | $1.01 \times 10^{6}$ | **x1.4**   | Standard Library      |
| `get(K)`            | $1.56 \times 10^{6}$ | $1.24 \times 10^{6}$ | **x1.3**   | Standard Library      |
| `getOrDefault(K,V)` | $1.50 \times 10^{6}$ | $1.33 \times 10^{6}$ | **x1.1**   | Standard Library      |
| `keySet()`          | $4.49 \times 10^{4}$ | $3.01 \times 10^{4}$ | **x1.5**   | Standard Library      |
| `put(K,V)`          | $3.78 \times 10^{6}$ | $3.77 \times 10^{6}$ | **x1.0**   | Standard Library      |
| `putAll(Map)`       | $2.21 \times 10^{6}$ | $1.78 \times 10^{6}$ | **x1.2**   | Standard Library      |
| `putIfAbsent(K,V)`  | $3.58 \times 10^{6}$ | $3.32 \times 10^{6}$ | **x1.1**   | Standard Library      |
| `remove(K)`         | $2.01 \times 10^{6}$ | $1.65 \times 10^{6}$ | **x1.2**   | Standard Library      |
| `remove(K,V)`       | $3.47 \times 10^{6}$ | $3.10 \times 10^{6}$ | **x1.1**   | Standard Library      |
| `replace(K,V)`      | $3.66 \times 10^{6}$ | $2.90 \times 10^{6}$ | **x1.3**   | Standard Library      |
| `replace(K,V,V)`    | $5.62 \times 10^{6}$ | $4.35 \times 10^{6}$ | **x1.3**   | Standard Library      |
| `toString()`        | $3.62 \times 10^{6}$ | $2.50 \times 10^{6}$ | **x1.4**   | Standard Library      |
| `values()`          | $3.52 \times 10^{4}$ | $9.31 \times 10^{3}$ | **x3.8**   | Standard Library      |

# Performance Charts

![clear().png](PerformanceCharts/clear%28%29.png)
![containsKey(K).png](PerformanceCharts/containsKey%28K%29.png)
![containsValue(V).png](PerformanceCharts/containsValue%28V%29.png)
![entryset().png](PerformanceCharts/entrySet%28%29.png)
![equals(Object o).png](PerformanceCharts/equals%28Object%20o%29.png)
![get(K).png](PerformanceCharts/get%28K%29.png)
![getOrDefault(K,V).png](PerformanceCharts/getOrDefault%28K%2CV%29.png)
![keySet().png](PerformanceCharts/keySet%28%29.png)
![put(K,V).png](PerformanceCharts/put%28K%2CV%29.png)
![putAll(Map).png](PerformanceCharts/putAll%28Map%29.png)
![putIfAbsent(K,V).png](PerformanceCharts/putIfAbsent%28K%2CV%29.png)
![remove(K).png](PerformanceCharts/remove%28K%29.png)
![remove(K,V).png](PerformanceCharts/remove%28K%2CV%29.png)
![replace(K,V).png](PerformanceCharts/replace%28K%2CV%29.png)
![replace(K,V,V).png](PerformanceCharts/replace%28K%2CV%2CV%29.png)
![toString().png](PerformanceCharts/toString%28%29.png)
![values().png](PerformanceCharts/values%28%29.png)

