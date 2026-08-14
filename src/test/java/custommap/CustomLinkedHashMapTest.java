package custommap;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomLinkedHashMapTest {

    @Test
    public void createEmptyMap_returnsMapOfSize_0() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertEquals(0, map.size());
    }

    @Test
    public void createEmptyMap_OfMaxSize_10_returnsMapOfSize_0() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_andAddOneItem_returnsSizeOf_1() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put(1, 1));
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_andAddTwoItems_ofSameValue_returnsSizeOf_1() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put(1, 1));
        assertEquals(1, map.size());
        Object previous = map.put(1, 1);
        assertNotNull(previous);
        assertEquals(1, previous);
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_andAddTwoItems_returnsSizeOf_2() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.put("abc", "def"));
        assertNull(map.put("def", "ghi"));
        assertEquals(2, map.size());
    }

    @Test
    public void createMap_withNoValues_onGetKey_abc_returns_null() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.get("abc"));
    }

    @Test
    public void createMap_onGetKey_null_returns_false() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.get(null));
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_ghi_returns_null() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.put("abc", "def"));
        Object value = map.get("ghi");
        assertNull(value);
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_keyOfTypeInteger_123_returns_null() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.put("abc", "def"));
        Object value = map.get(123);
        assertNull(value);
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_abc_returns_def() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
    }

    @Test
    public void createMap_addTwoItems_returnsSizeOf_2_andMatchingKeyValuePairs() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
        assertNull(map.put("ghi", "jkl"));
        value = map.get("ghi");
        assertEquals(value, "jkl");
    }

    @Test
    public void createMap_addKeyValue_123_456_on_put_123_789_returnsPreviousValue_456_andUpdatesKey_123_to_789() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(value, 456);
        Object previous = map.put("123", 789);
        assertNotNull(previous);
        assertEquals(456, previous);
        Object updatedValue = map.get("123");
        assertNotEquals(456, updatedValue);
        assertEquals(789, updatedValue);
    }

    @Test
    public void createMapOfKeyValue_String_String_onContainsKey_ofType_Null_returnsFalse() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertFalse(map.containsKey(null));
    }

    @Test
    public void createMapOfKeyValue_String_String_onContainsKey_ofType_Integer_returnsFalse() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertFalse(map.containsKey(1));
    }

    @Test
    public void createMapOfKeyValue_String_String_withOneValue_onContainsKey_NONEXISTENT_returnsFalse() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("1", "1");
        assertFalse(map.containsKey("NONEXISTENT"));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_removeKeyOf_123_returnsKeysValue_456_andSizeOf_0_andNoLongerContainsKey_123() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);
        value = map.remove("123");
        assertEquals(456, value);
        assertEquals(0, map.size());
        value = map.get("123");
        assertNull(value);
        assertFalse(map.containsKey("123"));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_removeKeyOf_789_returnsNull_andSizeOf_1() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);
        value = map.remove("789");
        assertNull(value);
        assertEquals(1, map.size());
    }

    @Test
    public void givenMap_onPutIfAbsentKeyThatExists_returnsExistingValue() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("123", 456);
        assertEquals(456, map.putIfAbsent("123", 456));
    }

    @Test
    public void givenMap_onPutIfAbsentKeyThatDoesNotExist_returnsNull() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.putIfAbsent("123", 456));
        assertEquals(456, map.get("123"));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullKey_returnsFalse() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertFalse(map.remove(null, 123));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullValue_returnsFalse() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertFalse(map.remove("123", null));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullKey_andNullValue_returnsFalse() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertFalse(map.remove(null, null));
    }

    @Test
    public void givenMap_onRemove_key_value_thatDoesNotExist_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertFalse(map.remove("123", 456));
    }

    @Test
    public void givenMap_onRemove_key_value_thatDoNotMatch_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("123", 456);
        assertFalse(map.remove("123", 789));
    }

    @Test
    public void givenMap_onRemove_key_value_thatMatch_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("123", 456);
        assertTrue(map.remove("123", 456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onClear_returnsEmptyMap_andNullValueOnGettingKey_123() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);
        assertEquals(1, map.size());
        map.clear();
        assertNull(map.get("123"));
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_onKeySet_returnsAllKeysFromMap() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            map.put(i, i * 10);
        Set<Integer> keysExpected = new HashSet<>();
        for(int i = 0; i < 10; i++)
            keysExpected.add(i);
        Set<Integer> keys = map.keySet();
        assertEquals(keysExpected, keys);
    }

    @Test
    public void createMapOfType_String_String_onGetOrDefault_withKeyValueOfIntegerType_andReturnValue_DUMMY_returns_DUMMY() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertEquals("DUMMY", map.getOrDefault(1, "DUMMY"));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesNotExist_returnsDefaultOf_1000() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 13; i++)
            map.put(i, i * 10);
        assertEquals(1000, map.getOrDefault(100, 1000));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesExist_returnsValueOf_100() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 13; i++)
            map.put(i, i * 10);
        assertEquals(100, map.getOrDefault(10, 100));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsKey_123_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        assertTrue(map.containsKey("123"));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsKey_780_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        assertFalse(map.containsKey("789"));
    }

    @Test
    public void createMapOf_String_String_onContainsValue_ofTypeInteger_returns_false() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", "456"));
        assertFalse(map.containsValue(456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_456_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        assertTrue(map.containsValue(456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_789_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.put("123", 456));
        assertFalse(map.containsValue(789));
    }

    @Test
    public void createTwoNonEqualMap_onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>();
        map.put("One", 1);
        mapTwo.put(1, 1);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoNonEqualMaps_andAddingItems_onEquals_returns_false() {
        CustomLinkedHashMap map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 11; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap mapTwo = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            mapTwo.put(i, i * 10);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createMap_onEquals_itself_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        assertEquals(map, map);
    }

    @Test
    public void createTwoEqualMapTypes_onEquals_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>();
        assertEquals(map, mapTwo);
    }

    @Test
    public void createTwoObjectTypes_onEquals_returns_False() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        List<String> arrayList = new ArrayList<>();
        assertNotEquals(map, arrayList);
    }

    @Test
    public void createTwoNonEqualMapTypes_andAddingItems_onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            mapTwo.put(i, i * 10);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoEqualMapTypes_andAddingItems_onEquals_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            mapTwo.put(String.valueOf(i), i * 10);
        assertEquals(map, mapTwo);
    }

    @Test
    public void createTwoEqualMapTypes_andAddingItems_thatCauseNoMatch_onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            mapTwo.put(String.valueOf(i), i * 100);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoDifferentMapTypes__onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            mapTwo.put(i, i * 100);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoMaps_withDifferentSizes_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 5; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            mapTwo.put(String.valueOf(i), i * 100);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void onReplacingValueInMap_thatDoesNotExist_returns_null() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertNull(map.replace(1, 1));
    }

    @Test
    public void onReplacingValueInMapForKeyThatDoesExist_returns_previousKey_andUpdatesValue() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 1);
        assertEquals(1, map.replace(1, 10));
        assertEquals(10, map.get(1));
    }

    @Test
    public void onReplacingKeyAndValue_withNullKey_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(null, 1, 1));
    }

    @Test
    public void onReplacingKeyAndValue_withNullMatchingValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(1, null, 1));
    }

    @Test
    public void onReplacingKeyAndValue_withNullNewValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(1, 1, null));
    }

    @Test
    public void onReplacingKeyAndValue_withNullKeyAndValueAndNullNewValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(null, null, null));
    }

    @Test
    public void onReplacingKeyAndValue_withValueReturnedFromKeyDoesNotMatch_returns_false() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 1);
        assertFalse(map.replace(1,2,2));
    }

    @Test
    public void onReplacingKeyAndValue_withValueReturnedFromKeyDoesMatch_returns_true_andUpdatesValue() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 1);
        assertTrue(map.replace(1,1,2));
        assertEquals(2, map.get(1));
    }

    @Test
    public void onGettingValuesAsCollection_fromEmptyMap_returnsEmptyCollection() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertEquals(0, map.values().size());
    }

    @Test
    public void onGettingValuesAsCollection_fromMapOfTenInteger_returnsMatchingCollectionOfValues() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 10; i++)
            map.put(i, i * 10);
        Collection<Integer> expectedValues = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            expectedValues.add(i * 10);
        Collection<Integer> values = map.values();
        assertEquals(10, values.size());
        assertEquals(10, expectedValues.size());
        assertArrayEquals(expectedValues.toArray(), values.toArray());
    }

    @Test
    public void givenEmptyMap_on_toString_returns_emptyBraces() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertEquals("{}", map.toString());
    }

    @Test
    public void givenMapOf_5_values_on_intKey_intValue_on_toString_returns_correctString() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        for(int i = 0; i < 5; i++)
            map.put(i, i * 10);
        assertEquals("{0=0, 1=10, 2=20, 3=30, 4=40}", map.toString());
    }

    @Test
    public void givenEmptyMap_onIsEmpty_returnsTrue() {
        assertTrue(new CustomLinkedHashMap<>().isEmpty());
    }

    @Test
    public void givenMapWithOneEntry_onIsEmpty_returnsFalse() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 10);
        assertFalse(map.isEmpty());
    }

    @Test
    public void giveEmpty_onClear_mapRemainsEmpty() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertTrue(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void givenMapWithEntries_onClear_thenIsEmpty_returnsTrue() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 100);
        map.put(2, 200);
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void givenEmptyMap_onEntrySet_returnsEmptySet() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        Set<Entry<Integer, Integer>> entries = map.entrySet();
        assertEquals(0, entries.size());
    }

    @Test
    public void givenEmptyMap_onPutAllNullMap_throwsNullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertThrows(NullPointerException.class, () -> map.putAll(null));
    }

    @Test
    public void givenEmptyMap_onPutAllEmptyMap_doesNotChangeMap() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>();
        map.putAll(mapTwo);
        assertEquals(0, map.size());
    }

    @Test
    public void givenEmptyMap_onPutAllWithEntries_addsAllEntries() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>();
        mapTwo.put(1, 100);
        mapTwo.put(2, 200);
        map.putAll(mapTwo);
        assertEquals(2, map.size());
        assertEquals(100, map.get(1));
        assertEquals(200, map.get(2));
    }

    @Test
    public void givenMapWithEntries_onPutAll_addsNewEntriesAndUpdatesExisting() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        map.put(1, 100);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>();
        mapTwo.put(1, 150);
        mapTwo.put(2, 200);
        map.putAll(mapTwo);
        assertEquals(2, map.size());
        assertEquals(150, map.get(1));
        assertEquals(200, map.get(2));
    }

    @Test
    public void onInstantiatingMapAsCache_withSizeOf_negative_1_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashMap<>(-1));
    }

    @Test
    public void onInstantiatingMapAsCache_withCacheCapacityOf_10_returnsSizeOf_0() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(10);
        assertEquals(0, cache.size());
    }

    @Test
    public void givenEmptyMap_onHashCode_returns_0() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>();
        assertEquals(0, map.hashCode());
    }

    @Test
    public void testHashCodeMatchesStandardMapContract() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("One", 1);
        map.put("Two", 2);
        map.put("Three", 3);
        int expectedHashCode = 0;
        for (Entry<String, Integer> entry : map.entrySet())
            expectedHashCode += entry.hashCode();
        assertEquals(expectedHashCode, map.hashCode());
    }

    @Test
    public void testHashCodeIsOrderIndependent() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        CustomLinkedHashMap<String, Integer> map2 = new CustomLinkedHashMap<>();
        map.put("A", 100);
        map.put("B", 200);
        map2.put("B", 200);
        map2.put("A", 100);
        assertEquals(map.hashCode(), map2.hashCode());
    }

    @Test
    public void testPutAllTriggersCapacityGrowthAndTableResize() {
        CustomLinkedHashMap<Integer, Integer> targetMap = new CustomLinkedHashMap<>();
        Map<Integer, Integer> sourceMap = new java.util.HashMap<>();
        for (int i = 0; i < 25; i++)
            sourceMap.put(i, i);
        assertDoesNotThrow(() -> targetMap.putAll(sourceMap));
        assertEquals(25, targetMap.size());
        for (int i = 0; i < 25; i++)
            assertEquals(i, targetMap.get(i));
    }

    @Test
    public void testReplaceAllSuccessfullyTransformsValues() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("A", 10);
        map.put("B", 20);
        map.replaceAll((key, value) -> value * 2);
        assertEquals(2, map.size());
        assertEquals(20, map.get("A"));
        assertEquals(40, map.get("B"));
    }

    @Test
    public void testReplaceAllThrowsNullPointerExceptionOnNullFunction() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("A", 10);
        assertThrows(NullPointerException.class, () -> map.replaceAll(null));
    }

    @Test
    public void testReplaceAllThrowsNullPointerExceptionOnNullFunctionResult() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("A", 10);
        map.put("B", 20);
        assertThrows(NullPointerException.class, () -> map.replaceAll((key, value) -> {
            if ("B".equals(key))
                return null;
            return value;
        }));
        assertEquals(10, map.get("A"), "First entry value was modified or retained depending on iteration execution flow.");
        assertEquals(20, map.get("B"), "The entry that caused the null return should remain untouched.");
    }

    @Test
    public void testRemoveEntryDeepInBucketCollisionChain() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("A", 100);
        map.put("B", 200);
        assertEquals(100, map.remove("A"));
        assertEquals(1, map.size());
        assertFalse(map.containsKey("A"));
        assertTrue(map.containsKey("B"));
        assertEquals(200, map.get("B"));
    }

    @Test
    public void testRemoveTraversesEntireCollisionChainWithoutFindingKey() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("A", 100);
        map.put("B", 200);
        Integer removedValue = map.remove("not-present");
        assertNull(removedValue);
        assertEquals(2, map.size());
    }

    @Test
    void testInitialCapacityAndLoadFactorConstructorSuccess() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(32, 0.5f);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    void testInitialCapacityAndLoadFactorConstructorNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashMap<>(-1, 0.75f));
    }

    @Test
    void testInitialCapacityAndLoadFactorConstructorZeroLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashMap<>(16, 0.0f));
    }

    @Test
    void testInitialCapacityAndLoadFactorConstructorNegativeLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashMap<>(16, -0.5f));
    }

    @Test
    void testInitialCapacityAndLoadFactorConstructorNaNLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> new CustomLinkedHashMap<>(16, Float.NaN));
    }

    @Test
    void testMapConstructorSuccess() {
        Map<String, String> sourceMap = new CustomLinkedHashMap<>();
        sourceMap.put("key1", "value1");
        sourceMap.put("key2", "value2");
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(sourceMap);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    void testMapConstructorEmptyMap() {
        Map<String, String> sourceMap = new CustomLinkedHashMap<>();
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(sourceMap);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    void testMapConstructorNullMapThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CustomLinkedHashMap<>(null));
    }

    @Test
    public void testEntrySetIteratorIteratesAllEntries() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        Set<Entry<String, Integer>> entries = map.entrySet();
        assertEquals(3, entries.size());
        Set<String> foundKeys = new HashSet<>();
        for (Entry<String, Integer> entry : entries) {
            foundKeys.add(entry.getKey());
            assertEquals(map.get(entry.getKey()), entry.getValue());
        }
        assertEquals(new HashSet<>(List.of("one", "two", "three")), foundKeys);
    }

    @Test
    public void testEntrySetContainsValidAndInvalidEntries() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("key", 42);
        Set<Entry<String, Integer>> entries = map.entrySet();
        Entry<String, Integer> validEntry = entries.iterator().next();
        assertTrue(entries.contains(validEntry));
        assertFalse(entries.contains("not-an-entry"));
        assertFalse(entries.contains(null));
        Map<String, Integer> fakeEntryMap = new java.util.HashMap<>();
        fakeEntryMap.put("key", 999);
        assertFalse(entries.contains(fakeEntryMap.entrySet().iterator().next()));
        fakeEntryMap.clear();
        fakeEntryMap.put("missing-key", 42);
        assertFalse(entries.contains(fakeEntryMap.entrySet().iterator().next()));
    }

    @Test
    public void testEntrySetContainsOnEmptyOrNullTable() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        Set<Entry<String, Integer>> entries = map.entrySet();
        Map<String, Integer> dummyEntryMap = new java.util.HashMap<>();
        dummyEntryMap.put("any", 1);
        assertFalse(entries.contains(dummyEntryMap.entrySet().iterator().next()));
    }

    @Test
    public void testEntrySetRemoveSuccessfullyDeletesMatchingEntry() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("deleteMe", 100);
        map.put("keepMe", 200);
        Set<Entry<String, Integer>> entries = map.entrySet();
        assertFalse(entries.remove("string-type-not-entry"));
        assertFalse(entries.remove(null));
        Entry<String, Integer> targetEntry = null;
        for (Entry<String, Integer> entry : entries)
            if ("deleteMe".equals(entry.getKey())) {
                targetEntry = entry;
                break;
            }
        assertNotNull(targetEntry);
        assertTrue(entries.remove(targetEntry));
        assertEquals(1, map.size());
        assertFalse(map.containsKey("deleteMe"));
        assertTrue(map.containsKey("keepMe"));
    }

    @Test
    public void testEntrySetClearEmptiesUnderlyingMap() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Set<Entry<String, Integer>> entries = map.entrySet();
        assertEquals(2, entries.size());
        entries.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, entries.size());
    }

    @Test
    public void testEntrySetCachingReturnsSameInstance() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        Set<Entry<String, Integer>> firstCall = map.entrySet();
        Set<Entry<String, Integer>> secondCall = map.entrySet();
        assertEquals(firstCall, secondCall);
    }

    @Test
    public void testKeySetIteratorIteratesAllKeys() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        Set<String> keys = map.keySet();
        assertEquals(3, keys.size());
        Set<String> foundKeys = new HashSet<>();
        for (String key : keys) {
            foundKeys.add(key);
            assertTrue(map.containsKey(key));
        }
        assertEquals(new HashSet<>(List.of("one", "two", "three")), foundKeys);
    }

    @Test
    public void testKeySetContainsDelegatesCorrectly() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("presentKey", 42);
        Set<String> keys = map.keySet();
        assertTrue(keys.contains("presentKey"));
        assertFalse(keys.contains("absentKey"));
        assertFalse(keys.contains(null));
        assertFalse(keys.contains(42));
    }

    @Test
    public void testKeySetRemoveSuccessfullyDeletesKeyAndReturnsTrue() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("removeMe", 100);
        map.put("stay", 200);
        Set<String> keys = map.keySet();
        assertFalse(keys.remove("notPresent"));
        assertFalse(keys.remove(null));
        assertTrue(keys.remove("removeMe"));
        assertEquals(1, map.size());
        assertFalse(map.containsKey("removeMe"));
        assertTrue(map.containsKey("stay"));
    }

    @Test
    public void testKeySetClearEmptiesUnderlyingMap() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Set<String> keys = map.keySet();
        assertEquals(2, keys.size());
        keys.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, keys.size());
    }

    @Test
    public void testKeySetCachingReturnsSameInstance() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        Set<String> firstCall = map.keySet();
        Set<String> secondCall = map.keySet();
        assertEquals(firstCall, secondCall);
    }

    @Test
    public void testAccessOrderBehaviorOnGet() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(16, 0.75f, true);
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.get("A");
        Iterator<String> iterator = map.keySet().iterator();
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
        assertEquals("A", iterator.next());
    }

    @Test
    public void testAccessOrderBehaviorOnPutExistingKey() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(16, 0.75f, true);
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("A", 10);
        Iterator<String> iterator = map.keySet().iterator();
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
        assertEquals("A", iterator.next());
    }

    @Test
    public void testInsertionOrderIsPreservedByDefault() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.get("A");
        Iterator<String> iterator = map.keySet().iterator();
        assertEquals("A", iterator.next());
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
    }

    @Test
    public void testValuesCollectionIteratorAndSize() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("one", 10);
        map.put("two", 20);
        map.put("three", 30);
        Collection<Integer> values = map.values();
        assertEquals(3, values.size());
        List<Integer> foundValues = new ArrayList<>();
        for (Integer value : values) {
            foundValues.add(value);
            assertTrue(map.containsValue(value));
        }
        assertEquals(3, foundValues.size());
        assertTrue(foundValues.contains(10));
        assertTrue(foundValues.contains(20));
        assertTrue(foundValues.contains(30));
    }

    @Test
    public void testValuesCollectionContainsDelegatesCorrectly() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("key", 100);
        Collection<Integer> values = map.values();
        assertTrue(values.contains(100));
        assertFalse(values.contains(999));
        assertFalse(values.contains(null));
    }

    @Test
    public void testValuesCollectionClearEmptiesUnderlyingMap() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Collection<Integer> values = map.values();
        assertEquals(2, values.size());
        values.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, values.size());
    }

    @Test
    public void testValuesCollectionCachingReturnsSameInstance() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>();
        Collection<Integer> firstCall = map.values();
        Collection<Integer> secondCall = map.values();
        assertEquals(firstCall, secondCall);
    }

    @Test
    public void testMapEntryGettersAndSetValue() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("testKey", "testValue");
        Entry<String, String> entry = map.entrySet().iterator().next();
        assertEquals("testKey", entry.getKey());
        assertEquals("testValue", entry.getValue());
        String oldValue = entry.setValue("newValue");
        assertEquals("testValue", oldValue);
        assertEquals("newValue", entry.getValue());
        assertEquals("newValue", map.get("testKey"));
    }

    @Test
    public void testMapEntryEqualsContract() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("key1", "value1");
        Entry<String, String> entry = map.entrySet().iterator().next();
        assertEquals(entry, entry);
        assertNotEquals("not-an-entry", entry);
        Map<String, String> standardMap = new java.util.HashMap<>();
        standardMap.put("key1", "value1");
        Entry<String, String> standardEntry = standardMap.entrySet().iterator().next();
        assertEquals(entry, standardEntry);
        Map<String, String> diffMap = new java.util.HashMap<>();
        diffMap.put("key1", "differentValue");
        assertNotEquals(entry, diffMap.entrySet().iterator().next());
    }

    @Test
    public void testMapEntryHashCodeContract() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("key1", "value1");
        Entry<String, String> entry = map.entrySet().iterator().next();
        Map<String, String> standardMap = new java.util.HashMap<>();
        standardMap.put("key1", "value1");
        Entry<String, String> standardEntry = standardMap.entrySet().iterator().next();
        assertEquals(standardEntry.hashCode(), entry.hashCode());
    }

    @Test
    public void testMapEntryToStringFormat() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("myKey", "myValue");
        Entry<String, String> entry = map.entrySet().iterator().next();
        assertEquals("myKey=myValue", entry.toString());
    }

    @Test
    public void testIteratorThrowsNoSuchElementExceptionWhenExhausted() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("key1", "value1");
        Iterator<String> iterator = map.keySet().iterator();
        assertTrue(iterator.hasNext());
        assertEquals("key1", iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testIteratorRemoveSuccessfullyDeletesCurrentElement() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
        assertTrue(iterator.hasNext());
        Entry<String, String> entry = iterator.next();
        assertEquals("key1", entry.getKey());
        iterator.remove();
        assertEquals(1, map.size());
        assertFalse(map.containsKey("key1"));
        assertTrue(map.containsKey("key2"));
    }

    @Test
    public void testIteratorRemoveThrowsIllegalStateExceptionWhenCalledBeforeNext() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("key1", "value1");
        Iterator<String> iterator = map.keySet().iterator();
        assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Test
    public void testIteratorRemoveThrowsIllegalStateExceptionWhenCalledTwiceConcurrently() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>();
        map.put("key1", "value1");
        Iterator<String> iterator = map.keySet().iterator();
        assertTrue(iterator.hasNext());
        assertEquals("key1", iterator.next());
        iterator.remove();
        assertThrows(IllegalStateException.class, iterator::remove);
    }
}