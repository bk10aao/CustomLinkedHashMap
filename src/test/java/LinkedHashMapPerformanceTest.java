import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class LinkedHashMapPerformanceTest {

    // Controls how many iterations are used to compute a stable average
    private static final int ITERATIONS = 100;
    private static final int STRUCTURAL_ITERATIONS = 10;
    private static final int WARMUP_RUNS = 20000;

    // Blackholes to ensure JVM doesn't optimize away calls
    private static long longBlackhole = 0;
    private static boolean boolBlackhole = false;
    private static int intBlackhole = 0;
    private static Object objBlackhole = null;

    public static void main(String[] args) {
        int[] sizes = { 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000 };

        long[][] results = new long[sizes.length][];
        Random random = new Random();

        System.out.println("Warming up JIT Compiler...");
        runGlobalWarmup(random);
        System.out.println("Warm-up complete. Starting full execution.");

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            System.out.println("Benchmarking size: " + size);

            // Exactly 23 methods accounted for:
            long putTime = benchmarkPut(size, random);                                 // 1
            long getTime = benchmarkGet(size, random);                                 // 2
            long getOrDefaultTime = benchmarkGetOrDefault(size, random);               // 3
            long removeTime = benchmarkRemove(size, random);                           // 4
            long removeWithValueTime = benchmarkRemoveWithValue(size, random);         // 5
            long containsKeyTime = benchmarkContainsKey(size, random);                 // 6
            long containsValueTime = benchmarkContainsValue(size, random);             // 7
            long putIfAbsentTime = benchmarkPutIfAbsent(size, random);                 // 8
            long replaceTime = benchmarkReplace(size, random);                         // 9
            long replaceWithOldNewTime = benchmarkReplaceWithOldNew(size, random);     // 10
            long keySetTime = benchmarkKeySet(size);                                   // 11
            long valuesTime = benchmarkValues(size);                                   // 12
            long clearTime = benchmarkClear(size);                                     // 13
            long equalsTime = benchmarkEquals(size);                                   // 14
            long toStringTime = benchmarkToString(size);                               // 15
            long entrySetTime = benchmarkEntrySet(size);                               // 16
            long putAllTime = benchmarkPutAll(size, random);                           // 17
            long computeTime = benchmarkCompute(size, random);                         // 18
            long computeIfAbsentTime = benchmarkComputeIfAbsent(size, random);         // 19
            long computeIfPresentTime = benchmarkComputeIfPresent(size, random);       // 20
            long forEachTime = benchmarkForEach(size);                                 // 21
            long mergeTime = benchmarkMerge(size, random);                             // 22
            long replaceAllTime = benchmarkReplaceAll(size);                           // 23

            results[i] = new long[]{
                    size, putTime, getTime, getOrDefaultTime, removeTime, removeWithValueTime,
                    containsKeyTime, containsValueTime, putIfAbsentTime, replaceTime, replaceWithOldNewTime,
                    keySetTime, valuesTime, clearTime, equalsTime, toStringTime, entrySetTime, putAllTime,
                    computeTime, computeIfAbsentTime, computeIfPresentTime, forEachTime, mergeTime, replaceAllTime
            };
        }

        writeResultsToCSV(sizes, results);

        if (boolBlackhole && longBlackhole == 9999) {
            System.out.println("Sink data checksum: " + intBlackhole);
        }
    }

    private static void runGlobalWarmup(Random random) {
        LinkedHashMap<Integer, String> warmMap = new LinkedHashMap<>();
        for (int i = 0; i < WARMUP_RUNS; i++) {
            warmMap.put(i, "Warm" + i);
            boolBlackhole ^= warmMap.containsKey(i);
            boolBlackhole ^= warmMap.containsValue("Warm" + i);
            objBlackhole = warmMap.get(i);
            objBlackhole = warmMap.getOrDefault(i, "Default");
            warmMap.putIfAbsent(i, "Absent");
            warmMap.replace(i, "Replaced");
            warmMap.replace(i, "Replaced", "Replaced2");
        }
        for (int i = 0; i < WARMUP_RUNS; i++) {
            warmMap.compute(i, (k, v) -> "Computed");
            warmMap.computeIfPresent(i, (k, v) -> "Present");
            warmMap.merge(i, "Merged", (v1, v2) -> v1 + v2);
            warmMap.remove(i);
        }
        System.gc();
    }

    // 1. put(K,V)
    private static long benchmarkPut(int size, Random random) {
        long totalElapsedTime = 0;
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            populateMap(map, size - 1);
            long start = System.nanoTime();
            map.put(random.nextInt(size * 2), "NewValue");
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 2. get(K)
    private static long benchmarkGet(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size);
            long start = System.nanoTime();
            String val = map.get(key);
            totalElapsedTime += (System.nanoTime() - start);
            if (val != null) intBlackhole += val.length();
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 3. getOrDefault(K,V)
    private static long benchmarkGetOrDefault(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            String val = map.getOrDefault(key, "Default");
            totalElapsedTime += (System.nanoTime() - start);
            if (val != null) intBlackhole += val.length();
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 4. remove(K)
    private static long benchmarkRemove(int size, Random random) {
        long totalElapsedTime = 0;
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            populateMap(map, size);
            int key = random.nextInt(size);
            long start = System.nanoTime();
            map.remove(key);
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 5. remove(K,V)
    private static long benchmarkRemoveWithValue(int size, Random random) {
        long totalElapsedTime = 0;
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            populateMap(map, size);
            int key = random.nextInt(size);
            long start = System.nanoTime();
            map.remove(key, "Value" + key);
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 6. containsKey(K)
    private static long benchmarkContainsKey(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            boolean checked = map.containsKey(key);
            totalElapsedTime += (System.nanoTime() - start);
            boolBlackhole ^= checked;
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 7. containsValue(V)
    private static long benchmarkContainsValue(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            String val = "Value" + random.nextInt(size * 2);
            long start = System.nanoTime();
            boolean checked = map.containsValue(val);
            totalElapsedTime += (System.nanoTime() - start);
            boolBlackhole ^= checked;
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 8. putIfAbsent(K,V)
    private static long benchmarkPutIfAbsent(int size, Random random) {
        long totalElapsedTime = 0;
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            populateMap(map, size);
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            map.putIfAbsent(key, "AbsentValue");
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 9. replace(K,V)
    private static long benchmarkReplace(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            map.replace(key, "ReplacedValue");
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 10. replace(K,V,V)
    private static long benchmarkReplaceWithOldNew(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size);
            long start = System.nanoTime();
            map.replace(key, "Value" + key, "UpdatedValue");
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 11. keySet()
    private static long benchmarkKeySet(int size) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            long start = System.nanoTime();
            int resSize = map.keySet().size();
            totalElapsedTime += (System.nanoTime() - start);
            intBlackhole += resSize;
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 12. values()
    private static long benchmarkValues(int size) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            long start = System.nanoTime();
            int resSize = map.values().size();
            totalElapsedTime += (System.nanoTime() - start);
            intBlackhole += resSize;
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 13. clear()
    private static long benchmarkClear(int size) {
        long totalElapsedTime = 0;
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            populateMap(map, size);
            long start = System.nanoTime();
            map.clear();
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 14. equals(Object o)
    private static long benchmarkEquals(int size) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map1 = new LinkedHashMap<>();
        LinkedHashMap<Integer, String> map2 = new LinkedHashMap<>();
        populateMap(map1, size);
        populateMap(map2, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            long start = System.nanoTime();
            boolean checked = map1.equals(map2);
            totalElapsedTime += (System.nanoTime() - start);
            boolBlackhole ^= checked;
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 15. toString()
    private static long benchmarkToString(int size) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            long start = System.nanoTime();
            String s = map.toString();
            totalElapsedTime += (System.nanoTime() - start);
            intBlackhole += s.length();
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 16. entrySet()
    private static long benchmarkEntrySet(int size) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            long start = System.nanoTime();
            int resSize = map.entrySet().size();
            totalElapsedTime += (System.nanoTime() - start);
            intBlackhole += resSize;
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 17. putAll(Map)
    private static long benchmarkPutAll(int size, Random random) {
        long totalElapsedTime = 0;
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            Map<Integer, String> source = new HashMap<>();
            for (int i = 0; i < size; i++) {
                source.put(random.nextInt(size * 2), "Value" + i);
            }
            long start = System.nanoTime();
            map.putAll(source);
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 18. compute(K,BiFunction)
    private static long benchmarkCompute(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            map.compute(key, (k, v) -> v == null ? "Computed" : v + "!");
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 19. computeIfAbsent(K,Function)
    private static long benchmarkComputeIfAbsent(int size, Random random) {
        long totalElapsedTime = 0;
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            populateMap(map, size);
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            map.computeIfAbsent(key, k -> "AbsentNew");
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 20. computeIfPresent(K,BiFunction)
    private static long benchmarkComputeIfPresent(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            map.computeIfPresent(key, (k, v) -> v + "Present");
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 21. forEach(BiConsumer)
    private static long benchmarkForEach(int size) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            long start = System.nanoTime();
            map.forEach((k, v) -> intBlackhole += k);
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    // 22. merge(K,V,BiFunction)
    private static long benchmarkMerge(int size, Random random) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            int key = random.nextInt(size * 2);
            long start = System.nanoTime();
            map.merge(key, "MergedVal", (v1, v2) -> v1 + v2);
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / ITERATIONS;
    }

    // 23. replaceAll(BiFunction)
    private static long benchmarkReplaceAll(int size) {
        long totalElapsedTime = 0;
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        populateMap(map, size);
        for (int iter = 0; iter < STRUCTURAL_ITERATIONS; iter++) {
            long start = System.nanoTime();
            map.replaceAll((k, v) -> "Updated" + v);
            totalElapsedTime += (System.nanoTime() - start);
        }
        return totalElapsedTime / STRUCTURAL_ITERATIONS;
    }

    private static void populateMap(LinkedHashMap<Integer, String> map, int size) {
        for (int i = 0; i < size; i++) {
            map.put(i, "Value" + i);
        }
    }

    private static void writeResultsToCSV(int[] sizes, long[][] results) {
        String csvFile = "LinkedHashMap_performance.csv";
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Size;put(K,V);get(K);getOrDefault(K,V);remove(K);remove(K,V);containsKey(K);"
                    + "containsValue(V);putIfAbsent(K,V);replace(K,V);replace(K,V,V);keySet();values();"
                    + "clear();equals(Object o);toString();entrySet();putAll(Map);compute(K,BiFunction);"
                    + "computeIfAbsent(K,Function);computeIfPresent(K,BiFunction);forEach(BiConsumer);"
                    + "merge(K,V,BiFunction);replaceAll(BiFunction)\n");

            for (long[] row : results) {
                StringBuilder sb = new StringBuilder();
                sb.append(row[0]);
                for (int j = 1; j < row.length; j++) {
                    sb.append(";").append(row[j]);
                }
                sb.append("\n");
                writer.append(sb.toString());
            }
            System.out.println("Results successfully documented in " + csvFile);
        } catch (IOException e) {
            System.err.println("Failed to write performance records to CSV file: " + e.getMessage());
        }
    }
}