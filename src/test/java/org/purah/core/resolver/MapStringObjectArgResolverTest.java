package org.purah.core.resolver;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.CheckInstance;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapStringObjectArgResolverTest {
    MapStringObjectArgResolver resolver = new MapStringObjectArgResolver();

   public static class TestStringObjectMap extends HashMap<String, Object> {

    }

    static class TestLongObjectMap extends HashMap<Long, Object> {

    }

    @Test
    void support() {
        MapStringObjectArgResolver mapStringObjectArgResolver = new MapStringObjectArgResolver();
        Assertions.assertTrue(mapStringObjectArgResolver.support(TestStringObjectMap.class));
        Assertions.assertFalse(mapStringObjectArgResolver.support(TestLongObjectMap.class));
    }

    @Test
    void getFieldsObjectMap() {
        TestStringObjectMap map = new TestStringObjectMap();
        map.put("a", "a");
        map.put("ab", "ab");
        Map<String, CheckInstance> fieldsObjectMap = resolver.getFieldsObjectMap(map, Sets.newHashSet("ab"));


        assertEquals(1, fieldsObjectMap.size());
        assertEquals("ab", fieldsObjectMap.get("ab").instance());

    }

    @Test
    void fields() {
        TestStringObjectMap map = new TestStringObjectMap();
        map.put("a", "a");
        map.put("ab", "ab");
        Assertions.assertEquals(resolver.fields(map), map.keySet());
    }
}