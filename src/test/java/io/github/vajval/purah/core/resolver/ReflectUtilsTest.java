package io.github.vajval.purah.core.resolver;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.util.People;
import io.github.vajval.purah.util.TestUser;

import static com.google.common.collect.Sets.newHashSet;

class ReflectUtilsTest {
    @Test
    public void test() {
        Sets.newHashSet();
        TestUser testUser = new TestUser(1L, "testUser", "address");
        Assertions.assertTrue(ReflectUtils.noExtendEnabledFields(TestUser.class, newHashSet("id", "name")));

        Assertions.assertFalse(ReflectUtils.noExtendEnabledFields(TestUser.class, newHashSet("id", "name", "people.id")));
        Assertions.assertFalse(ReflectUtils.noExtendEnabledFields(TestUser.class, newHashSet("id", "name", "people.child#0.id")));

        Assertions.assertFalse(ReflectUtils.noExtendEnabledFields(People.class, newHashSet("child#0.id")));
        Assertions.assertTrue(ReflectUtils.noExtendEnabledFields(People.class, newHashSet("address","name")));

    }

}