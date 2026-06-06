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

|  Method             |    CustomLinkedHashMap (ns)    |    LinkedHashMap (ns)    | Multiplier  | Faster Implementation  |
|---------------------|:------------------------------:|:------------------------:|:-----------:|:----------------------:|
| `clear()`           |      $1.42 \times 10^{6}$      |   $1.23 \times 10^{5}$   |  **x11.6**  |    Standard Library    |
| `containsKey(K)`    |      $1.63 \times 10^{6}$      |   $1.13 \times 10^{6}$   |  **x1.4**   |    Standard Library    |
| `containsValue(V)`  |      $5.46 \times 10^{9}$      |   $4.84 \times 10^{9}$   |  **x1.1**   |    Standard Library    |
| `entrySet()`        |      $1.01 \times 10^{3}$      |   $8.29 \times 10^{2}$   |  **x1.2**   |    Standard Library    |
| `equals(Object o)`  |      $1.11 \times 10^{6}$      |   $9.53 \times 10^{5}$   |  **x1.2**   |    Standard Library    |
| `get(K)`            |      $1.51 \times 10^{6}$      |   $1.35 \times 10^{6}$   |  **x1.1**   |    Standard Library    |
| `getOrDefault(K,V)` |      $1.55 \times 10^{6}$      |   $1.31 \times 10^{6}$   |  **x1.2**   |    Standard Library    |
| `keySet()`          |      $3.57 \times 10^{4}$      |   $1.08 \times 10^{4}$   |  **x3.3**   |    Standard Library    |
| `put(K,V)`          |      $4.28 \times 10^{6}$      |   $3.62 \times 10^{6}$   |  **x1.2**   |    Standard Library    |
| `putAll(Map)`       |      $2.51 \times 10^{6}$      |   $1.63 \times 10^{6}$   |  **x1.5**   |    Standard Library    |
| `putIfAbsent(K,V)`  |      $4.19 \times 10^{6}$      |   $3.27 \times 10^{6}$   |  **x1.3**   |    Standard Library    |
| `remove(K)`         |      $2.04 \times 10^{6}$      |   $1.49 \times 10^{6}$   |  **x1.4**   |    Standard Library    |
| `remove(K,V)`       |      $5.10 \times 10^{6}$      |   $2.80 \times 10^{6}$   |  **x1.8**   |    Standard Library    |
| `replace(K,V)`      |      $3.87 \times 10^{6}$      |   $3.02 \times 10^{6}$   |  **x1.3**   |    Standard Library    |
| `replace(K,V,V)`    |      $5.69 \times 10^{6}$      |   $4.64 \times 10^{6}$   |  **x1.2**   |    Standard Library    |
| `toString()`        |      $3.29 \times 10^{6}$      |   $2.54 \times 10^{6}$   |  **x1.3**   |    Standard Library    |
| `values()`          |      $1.79 \times 10^{4}$      |   $6.17 \times 10^{3}$   |  **x2.9**   |    Standard Library    |


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

