import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_onPutKeyOf_null_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertThrows(NullPointerException.class, ()-> map.put(null, 1));
    }

    @Test
    public void createMap_andAddOneItem_returnsSizeOf_1() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertNull(map.put(1, 1));
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_andAddTwoItems_ofSameValue_returnsSizeOf_1() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertNull(map.put(1, 1));
        assertEquals(1, map.size());
        Object previous = map.put(1, 1);
        assertNotNull(previous);
        assertEquals(1, previous);
        assertEquals(1, map.size());
    }

    @Test
    public void createMap_andAddTwoItems_returnsSizeOf_2() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertNull(map.put("abc", "def"));
        assertNull(map.put("def", "ghi"));
        assertEquals(2, map.size());
    }

    @Test
    public void createMap_withNoValues_onGetKey_abc_returns_null() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertNull(map.get("abc"));
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_ghi_returns_null() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("ghi");
        assertNull(value);
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_keyOfTypeInteger_123_returns_null() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get(123);
        assertNull(value);
    }

    @Test
    public void createMap_addItemsWithKeyValue_abc_def_onGet_abc_returns_def() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
    }

    @Test
    public void createMap_addTwoItems_returnsSizeOf_2_andMatchingKeyValuePairs() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertNull(map.put("abc", "def"));
        Object value = map.get("abc");
        assertEquals("def", value);
        assertNull(map.put("ghi", "jkl"));
        value = map.get("ghi");
        assertEquals(value, "jkl");
    }

    @Test
    public void createMap_addKeyValue_123_456_on_put_123_789_returnsPreviousValue_456_andUpdatesKey_123_to_789() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
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
    public void createMapOfKeyValue_String_String_onRemoveKeyOfNull_throws_NullPointerException() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertThrows(NullPointerException.class, ()-> map.remove(null));
    }

    @Test
    public void createMapOfKeyValue_String_String_onContainsKey_ofType_Integer_returnsFalse() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertFalse(map.containsKey(1));
    }

    @Test
    public void createMap_addKeyValue_123_456_on_removeKeyOf_123_returnsKeysValue_456_andSizeOf_0_andNoLongerContainsKey_123() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
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
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);

        value = map.remove("789");
        assertNull(value);
        assertEquals(1, map.size());
    }

    @Test
    public void givenMap_onPutIfAbsent_withNullKey_throws_NullPonterException() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.putIfAbsent(null, 123));
    }

    @Test
    public void givenMap_onPutIfAbsent_withNullValue_throws_NullPointerException() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.putIfAbsent("123", null));
    }

    @Test
    public void givenMap_onPutIfAbsent_withNullKey_andNullValue_throws_NullPointerException() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.putIfAbsent(null, null));
    }

    @Test
    public void givenMap_onPutIfAbsentKeyThatExists_returnsExistingValue() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        map.put("123", 456);
        assertEquals(456, map.putIfAbsent("123", 456));
    }

    @Test
    public void givenMap_onPutIfAbsentKeyThatDoesNotExist_returnsNull() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertNull(map.putIfAbsent("123", 456));
        assertEquals(456, map.get("123"));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullKey_returnsFalse() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertFalse(map.remove(null, 123));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullValue_returnsFalse() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertFalse(map.remove("123", null));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullKey_andNullValue_returnsFalse() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertFalse(map.remove(null, null));
    }

    @Test
    public void givenMap_onRemove_key_value_thatDoesNotExist_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertFalse(map.remove("123", 456));
    }

    @Test
    public void givenMap_onRemove_key_value_thatDoNotMatch_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        map.put("123", 456);
        assertFalse(map.remove("123", 789));
    }

    @Test
    public void givenMap_onRemove_key_value_thatMatch_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        map.put("123", 456);
        assertTrue(map.remove("123", 456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onClear_returnsEmptyMap_andNullValueOnGettingKey_123() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertNull(map.put("123", 456));
        Object value = map.get("123");
        assertEquals(456, value);
        assertEquals(1, map.size());
        map.clear();
        value = map.get("123");
        assertNull(value);
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_onKeySet_returnsAllKeysFromMap() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for(int i = 0; i < 10; i++)
            map.put(i, i * 10);
        Set<Integer> keysExpected = new HashSet<>();
        for(int i = 0; i < 10; i++)
            keysExpected.add(i);
        Set<Integer> keys = map.keySet();
        assertEquals(keysExpected, keys);
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueOfNull_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.getOrDefault(null, null));
    }

    @Test
    public void createMapOfType_String_String_onGetOrDefault_withKeyValueOfIntegerType_andReturnValue_DUMMY_returns_DUMMY() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertEquals("DUMMY", map.getOrDefault(1, "DUMMY"));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesNotExist_returnsDefaultOf_1000() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for (int i = 0; i < 13; i++)
            map.put(i, i * 10);
        assertEquals(1000, map.getOrDefault(100, 1000));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesExist_returnsValueOf_100() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for (int i = 0; i < 13; i++)
            map.put(i, i * 10);
        assertEquals(100, map.getOrDefault(10, 1000));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsKey_123_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertTrue(map.containsKey("123"));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsKey_780_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertFalse(map.containsKey("789"));
    }

    @Test
    public void createMapOf_String_String_onContainsValue_ofTypeInteger_returns_false() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertNull(map.put("123", "456"));
        assertFalse(map.containsValue(456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_456_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertTrue(map.containsValue(456));
    }

    @Test
    public void createMap_addKeyValue_123_456_onContainsValue_789_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertNull(map.put("123", 456));
        assertFalse(map.containsValue(789));
    }

    @Test
    public void createTwoNonEqualMap_onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put("One", 1);
        mapTwo.put(1, 1);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoNonEqualMaps_andAddingItems_onEquals_returns_false() {
        CustomLinkedHashMap map = new CustomLinkedHashMap(String.class, Integer.class);
        for(int i = 0; i < 11; i++) map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap mapTwo = new CustomLinkedHashMap(Integer.class, Integer.class);
        for(int i = 0; i < 10; i++) mapTwo.put(i, i * 10);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createMap_onEquals_itself_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertEquals(map, map);
    }

    @Test
    public void createTwoEqualMapTypes_onEquals_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertEquals(map, mapTwo);
    }

    @Test
    public void createTwoObjectTypes_onEquals_returns_False() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        List<String> arrayList = new ArrayList<>();
        assertNotEquals(map, arrayList);
    }

    @Test
    public void createTwoNonEqualMapTypes_andAddingItems_onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for(int i = 0; i < 10; i++)
            mapTwo.put(i, i * 10);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoEqualMapTypes_andAddingItems_onEquals_returns_true() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 10; i++)
            mapTwo.put(String.valueOf(i), i * 10);
        assertEquals(map, mapTwo);
    }

    @Test
    public void createTwoEqualMapTypes_andAddingItems_thatCauseNoMatch_onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 10; i++)
            mapTwo.put(String.valueOf(i), i * 100);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoDifferentMapTypes__onEquals_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 10; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for(int i = 0; i < 10; i++)
            mapTwo.put(i, i * 100);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void createTwoMaps_withDifferentSizes_returns_false() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 5; i++)
            map.put(String.valueOf(i), i * 10);
        CustomLinkedHashMap<String, Integer> mapTwo = new CustomLinkedHashMap<>(String.class, Integer.class);
        for(int i = 0; i < 10; i++)
            mapTwo.put(String.valueOf(i), i * 100);
        assertNotEquals(map, mapTwo);
    }

    @Test
    public void onReplacingValueInMap_withNullKey_throws_NullPointerException() {
        CustomLinkedHashMap<String, String> map = new CustomLinkedHashMap<>(String.class, String.class);
        assertThrows(NullPointerException.class, ()-> map.replace("10", null));
    }

    @Test
    public void onReplacingValueInMap_withNullValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertThrows(NullPointerException.class, ()-> map.replace(null, 10));
    }

    @Test
    public void onReplacingValueInMap_withNullKeyAndValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertThrows(NullPointerException.class, ()-> map.replace(null, null));
    }

    @Test
    public void onReplacingValueInMap_thatDoesNotExist_returns_null() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertNull(map.replace(1, 1));
    }

    @Test
    public void onReplacingValueInMapForKeyThatDoesExist_returns_previousKey_andUpdatesValue() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 1);
        assertEquals(1, map.replace(1, 10));
        assertEquals(10, map.get(1));
    }

    @Test
    public void onReplacingKeyAndValue_withNullKey_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(null, 1, 1));
    }

    @Test
    public void onReplacingKeyAndValue_withNullMatchingValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(1, null, 1));
    }

    @Test
    public void onReplacingKeyAndValue_withNullNewValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(1, 1, null));
    }

    @Test
    public void onReplacingKeyAndValue_withNullKeyAndValueAndNullNewValue_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 1);
        assertThrows(NullPointerException.class, ()-> map.replace(null, null, null));
    }

    @Test
    public void onReplacingKeyAndValue_withValueReturnedFromKeyDoesNotMatch_returns_false() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 1);
        assertFalse(map.replace(1,2,2));
    }

    @Test
    public void onReplacingKeyAndValue_withValueReturnedFromKeyDoesMatch_returns_true_andUpdatesValue() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 1);
        assertTrue(map.replace(1,1,2));
        assertEquals(2, map.get(1));
    }

    @Test
    public void onGettingValuesAsCollection_fromEmptyMap_returnsEmptyCollection() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertEquals(0, map.values().size());
    }

    @Test
    public void onGettingValuesAsCollection_fromMapOfTenInteger_returnsMatchingCollectionOfValues() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
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
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertEquals("{}", map.toString());
    }

    @Test
    public void givenMapOf_5_values_on_intKey_intValue_on_toString_returns_correctString() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for(int i = 0; i < 5; i++)
            map.put(i, i * 10);
        assertEquals("{0=0, 1=10, 2=20, 3=30, 4=40}", map.toString());
    }

    @Test
    public void givenEmptyMap_onIsEmpty_returnsTrue() {
        assertTrue(new CustomLinkedHashMap<>(Integer.class, Integer.class).isEmpty());
    }

    @Test
    public void givenMapWithOneEntry_onIsEmpty_returnsFalse() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 10);
        assertFalse(map.isEmpty());
    }

    @Test
    public void givenMapWithEntries_onClear_thenIsEmpty_returnsTrue() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 100);
        map.put(2, 200);
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void givenEmptyMap_onEntrySet_returnsEmptySet() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        Set<CustomLinkedHashMap.Entry<Integer, Integer>> entries = map.entrySet();
        assertEquals(0, entries.size());
    }

    @Test
    public void givenEmptyMap_onPutAllNullMap_throwsNullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.putAll(null));
    }

    @Test
    public void givenEmptyMap_onPutAllEmptyMap_doesNotChangeMap() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.putAll(mapTwo);
        assertEquals(0, map.size());
    }

    @Test
    public void givenEmptyMap_onPutAllWithEntries_addsAllEntries() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        mapTwo.put(1, 100);
        mapTwo.put(2, 200);
        map.putAll(mapTwo);
        assertEquals(2, map.size());
        assertEquals(100, map.get(1));
        assertEquals(200, map.get(2));
    }

    @Test
    public void givenMapWithEntries_onPutAll_addsNewEntriesAndUpdatesExisting() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        map.put(1, 100);
        CustomLinkedHashMap<Integer, Integer> mapTwo = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        mapTwo.put(1, 150);
        mapTwo.put(2, 200);
        map.putAll(mapTwo);
        assertEquals(2, map.size());
        assertEquals(150, map.get(1));
        assertEquals(200, map.get(2));
    }

    @Test
    public void onInstantiatingMapAsCache_withSizeOf_negative_1_throws_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new CustomLinkedHashMap<>(Integer.class, Integer.class, -1, true)
        );
    }

    @Test
    public void onInstantiatingMapAsCache_withCacheCapacityOf_10_returnsSizeOf_0() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(Integer.class, Integer.class, 10, true);
        assertEquals(0, cache.size());
    }

    @Test
    public void givenMapAsCache_withValues_1_2_3_onGet_4_returns_null() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(Integer.class, Integer.class, 10, true);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        assertNull(cache.get(4));
    }

    @Test
    public void givenMapAsCache_withValues_1_2_3_onGet_3_returns_3() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(Integer.class, Integer.class, 10, true);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        assertEquals(3, cache.get(3));
    }

    @Test
    public void givenMapAsCache_withValues_1_2_3_onPut_4_4_returnsSizeOf_3() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(Integer.class, Integer.class, 3, true);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        assertEquals(3, cache.size());
    }

    @Test
    public void givenMapAsCacheOfSize_5_on_addingSizeValues_removesLeastUsedCacheValueOf_1_andReturns_on_get_1_returns_null() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(Integer.class, Integer.class, 5, true);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.put(4, 4);
        cache.put(5, 5);
        cache.put(6, 6);
        assertNull(cache.get(1));
        assertEquals(5, cache.size());
        assertFalse(cache.containsKey(1));
        assertFalse(cache.containsValue(1));
    }

    @Test
    public void givenLRUCache_whenReadingAnItem_itMovesToTheEndOfTheIterationLine() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(Integer.class, Integer.class, 3, true);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.get(1);
        Iterator<Integer> keyIterator = cache.keySet().iterator();
        assertEquals(2, keyIterator.next());
        assertEquals(3, keyIterator.next());
        assertEquals(1, keyIterator.next());
    }

    @Test
    public void givenLRUCache_whenReadingAnItemWithGetOrDefault_itMovesToTheEndOfTheIterationLine() {
        CustomLinkedHashMap<Integer, Integer> cache = new CustomLinkedHashMap<>(Integer.class, Integer.class, 3, true);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.getOrDefault(1, 100);
        Iterator<Integer> keyIterator = cache.keySet().iterator();
        assertEquals(2, keyIterator.next());
        assertEquals(3, keyIterator.next());
        assertEquals(1, keyIterator.next());
    }
}