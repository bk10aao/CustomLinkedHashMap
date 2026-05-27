import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedHashMapTest {

    @Test
    public void createEmptyMap_returnsMapOfSize_0() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertEquals(0, map.size());
    }

    @Test
    public void createMap_onPutKeyOf_null_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertThrows(IllegalArgumentException.class, ()-> map.put(null, 1));
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
    public void givenMap_onPutIfAbsent_withNullKey_throws_NullPointerException() {
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
    public void givenMap_onRemove_withKeyValue_withNullKey_throws_NullPointerException() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.remove(null, 123));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullValue_throws_NullPointerException() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.remove("123", null));
    }

    @Test
    public void givenMap_onRemove_withKeyValue_withNullKey_andNullValue_throws_NullPointerException() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.remove(null, null));
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
    public void createMap_addKeyValue_123_456_onContainsKey_null_throws_NullPointerException() {
        CustomLinkedHashMap<String, Integer> map = new CustomLinkedHashMap<>(String.class, Integer.class);
        assertThrows(NullPointerException.class, ()-> map.containsKey(null));
    }

    @Test
    public void createMap_onKeySet_returnsAllKeysFromMap() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for(int i = 0; i < 10; i++) map.put(i, i * 10);
        Set<Integer> keysExpected = new HashSet<>();
        for(int i = 0; i < 10; i++) keysExpected.add(i);
        Set<Integer> keys = map.keySet();
        assertEquals(keysExpected, keys);
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueOfNull_throws_NullPointerException() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        assertThrows(NullPointerException.class, () -> map.getOrDefault(null, null));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesNotExist_returnsDefaultOf_1000() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for (int i = 0; i < 13; i++) map.put(i, i * 10);
        assertEquals(1000, map.getOrDefault(100, 1000));
    }

    @Test
    public void createMap_onGetOrDefault_withKeyValueThatDoesExist_returnsValueOf_100() {
        CustomLinkedHashMap<Integer, Integer> map = new CustomLinkedHashMap<>(Integer.class, Integer.class);
        for (int i = 0; i < 13; i++) map.put(i, i * 10);
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
}