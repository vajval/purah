package com.purah.resolver;

import com.purah.checker.CheckInstance;
import com.purah.matcher.intf.FieldMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BaseArgResolverTest {
    static class TestBaseArgResolver extends BaseArgResolver<Map>{
        @Override
        public Map<String, CheckInstance> getMatchFieldObjectMap(Map s, FieldMatcher fieldMatcher) {
            return Collections.emptyMap();
        }
    }

    @Test
    void support() {
        TestBaseArgResolver testBaseArgResolver=new TestBaseArgResolver();
        Assertions.assertTrue(testBaseArgResolver.support(Map.class));
        Assertions.assertTrue(testBaseArgResolver.support(MapStringObjectArgResolverTest.TestStringObjectMap.class));
    }
}