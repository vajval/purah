package org.purah.core.resolver;

import org.junit.jupiter.api.Test;
import org.purah.util.People;
import org.purah.util.TestUser;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.purah.core.resolver.ReflectUtils.noExtendEnabledFields;

class ReflectUtilsTest {
    @Test
    public void test() {
        TestUser testUser = new TestUser(1L, "testUser", "address");
        assertTrue(noExtendEnabledFields(TestUser.class, newHashSet("id", "name")));

        assertFalse(noExtendEnabledFields(TestUser.class, newHashSet("id", "name", "people.id")));
        assertFalse(noExtendEnabledFields(TestUser.class, newHashSet("id", "name", "people.child#0.id")));

        assertFalse(noExtendEnabledFields(People.class, newHashSet("child#0.id")));
        assertTrue(noExtendEnabledFields(People.class, newHashSet("address","name")));

    }

}