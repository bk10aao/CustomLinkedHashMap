# Custom Linked HashMap

Implementation of a LinkedHashMap.

All methods implemented are identical to those found in the Java Map interface.

# Build and Test

To build and test the project run command `./gradlew clean build`

To test the project run command `./gradlew test`

# Time Complexity

| Method                       |  CustomLinkedHashMap  |  LinkedHashMap (JDK)  | Winner |
|------------------------------|:---------------------:|:---------------------:|:------:|
| **clear()**                  |         O(n)          |         O(n)          |  Tie   |
| **containsKey(Object)**      |         O(1)          |         O(1)          |  Tie   |
| **containsValue(Object)**    |         O(n)          |         O(n)          |  Tie   |
| **entrySet()**               |         O(1)          |         O(1)          |  Tie   |
| **equals(Object)**           |         O(n)          |         O(n)          |  Tie   |
| **get(Object)**              |         O(1)          |         O(1)          |  Tie   |
| **getOrDefault(Object, V)**  |         O(1)          |         O(1)          |  Tie   |
| **hashCode()**               |         O(n)          |         O(n)          |  Tie   |
| **isEmpty()**                |         O(1)          |         O(1)          |  Tie   |
| **keySet()**                 |         O(1)          |         O(1)          |  Tie   |
| **put(K, V)**                |         O(1)          |         O(1)          |  Tie   |
| **putAll(Map)**              |         O(m)          |         O(m)          |  Tie   |
| **putIfAbsent(K, V)**        |         O(1)          |         O(1)          |  Tie   |
| **remove(Object)**           |         O(1)          |         O(1)          |  Tie   |
| **remove(Object, Object)**   |         O(1)          |         O(1)          |  Tie   |
| **removeEldestEntry(Entry)** |         O(1)          |         O(1)          |  Tie   |
| **replace(K, V)**            |         O(1)          |         O(1)          |  Tie   |
| **replace(K, V, V)**         |         O(1)          |         O(1)          |  Tie   |
| **size()**                   |         O(1)          |         O(1)          |  Tie   |
| **toString()**               |         O(n)          |         O(n)          |  Tie   |
| **values()**                 |         O(1)          |         O(1)          |  Tie   |

# Space Complexity

| Method                       | CustomLinkedHashMap | LinkedHashMap (JDK) | Winner |
|------------------------------|:-------------------:|:-------------------:|:------:|
| **clear()**                  |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **containsKey(Object)**      |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **containsValue(Object)**    |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **entrySet()**               |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **equals(Object)**           |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **get(Object)**              |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **getOrDefault(Object, V)**  |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **hashCode()**               |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **isEmpty()**                |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **keySet()**                 |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **put(K, V)**                |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **putAll(Map)**              |   O(m) auxiliary    |   O(m) auxiliary    |  Tie   |
| **putIfAbsent(K, V)**        |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **remove(Object)**           |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **remove(Object, Object)**   |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **removeEldestEntry(Entry)** |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **replace(K, V)**            |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **replace(K, V, V)**         |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **size()**                   |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **toString()**               |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |
| **values()**                 |   O(1) auxiliary    |   O(1) auxiliary    |  Tie   |

**Notes**:
- **n**: Total number of key-value mappings currently in the map.
- **m**: Number of key-value mappings in the input map.

| Method              | CustomLinkedHashMap (ns) |  LinkedHashMap (ns)   | Multiplier | Faster Implementation |
|:--------------------|:------------------------:|:---------------------:|:----------:|:----------------------|
| `clear()`           |   $7.85 \times 10^{4}$   | $7.93 \times 10^{4}$  | **x0.99**  | CustomLinkedHashMap   |
| `containsKey(K)`    |   $1.59 \times 10^{6}$   | $1.24 \times 10^{6}$  | **x1.28**  | Standard Library      |
| `containsValue(V)`  |  $1.31 \times 10^{10}$   | $1.21 \times 10^{10}$ | **x1.08**  | Standard Library      |
| `entrySet()`        |   $9.58 \times 10^{2}$   | $5.83 \times 10^{2}$  | **x1.64**  | Standard Library      |
| `equals(Object o)`  |   $5.78 \times 10^{5}$   | $5.29 \times 10^{5}$  | **x1.09**  | Standard Library      |
| `get(K)`            |   $1.55 \times 10^{6}$   | $1.26 \times 10^{6}$  | **x1.22**  | Standard Library      |
| `getOrDefault(K,V)` |   $1.94 \times 10^{6}$   | $1.20 \times 10^{6}$  | **x1.61**  | Standard Library      |
| `keySet()`          |   $1.29 \times 10^{3}$   | $1.92 \times 10^{3}$  | **x0.67**  | CustomLinkedHashMap   |
| `put(K,V)`          |   $4.38 \times 10^{6}$   | $4.01 \times 10^{6}$  | **x1.09**  | Standard Library      |
| `putAll(Map)`       |   $2.26 \times 10^{6}$   | $1.76 \times 10^{6}$  | **x1.29**  | Standard Library      |
| `putIfAbsent(K,V)`  |   $4.74 \times 10^{6}$   | $4.63 \times 10^{6}$  | **x1.03**  | Standard Library      |
| `remove(K)`         |   $1.95 \times 10^{6}$   | $1.26 \times 10^{6}$  | **x1.55**  | Standard Library      |
| `remove(K,V)`       |   $4.12 \times 10^{6}$   | $2.85 \times 10^{6}$  | **x1.45**  | Standard Library      |
| `replace(K,V)`      |   $5.48 \times 10^{6}$   | $4.91 \times 10^{6}$  | **x1.12**  | Standard Library      |
| `replace(K,V,V)`    |   $7.49 \times 10^{6}$   | $7.50 \times 10^{6}$  | **x1.00**  | CustomLinkedHashMap   |
| `toString()`        |   $3.65 \times 10^{6}$   | $2.33 \times 10^{6}$  | **x1.57**  | Standard Library      |
| `values()`          |   $1.25 \times 10^{3}$   | $1.42 \times 10^{3}$  | **x0.88**  | CustomLinkedHashMap   |


# Performance Charts

![clear().png](PerformanceCharts/plot_clear__.png)
![containsKey(K).png](PerformanceCharts/plot_containsKey_K_.png)
![containsValue(V).png](PerformanceCharts/plot_containsValue_V_.png)
![entryset().png](PerformanceCharts/plot_entrySet__.png)
![equals(Object o).png](PerformanceCharts/plot_equals_Object_o_.png)
![get(K).png](PerformanceCharts/plot_get_K_.png)
![getOrDefault(K,V).png](PerformanceCharts/plot_getOrDefault_K_V_.png)
![keySet().png](PerformanceCharts/plot_keySet__.png)
![put(K,V).png](PerformanceCharts/plot_put_K_V_.png)
![putAll(Map).png](PerformanceCharts/plot_putAll_Map_.png)
![putIfAbsent(K,V).png](PerformanceCharts/plot_putIfAbsent_K_V_.png)
![remove(K).png](PerformanceCharts/plot_remove_K_.png)
![remove(K,V).png](PerformanceCharts/plot_remove_K_V_.png)
![replace(K,V).png](PerformanceCharts/plot_replace_K_V_.png)
![replace(K,V,V).png](PerformanceCharts/plot_replace_K_V_V_.png)
![toString().png](PerformanceCharts/plot_toString__.png)
![values().png](PerformanceCharts/plot_values__.png)

