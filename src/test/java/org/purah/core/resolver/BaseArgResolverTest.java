package org.purah.core.resolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.base.CheckInstance;
import org.purah.core.matcher.intf.FieldMatcher;

import java.util.Collections;
import java.util.Map;

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