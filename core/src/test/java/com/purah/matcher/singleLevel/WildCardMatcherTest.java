package com.purah.matcher.singleLevel;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.purah.matcher.intf.FieldMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class WildCardMatcherTest {


    static Set<String> testFields = Sets.newHashSet("a", "ab", "abc", "abcd", "ba");

    public static void assertMatch_X(FieldMatcher fieldMatcher) {

        Set<String> matchFields = fieldMatcher.matchFields(testFields);
        Assertions.assertEquals(matchFields, Sets.newHashSet("a", "ab", "abc", "abcd"));
    }
    public static void assertMatch_W(FieldMatcher fieldMatcher) {

        Set<String> matchFields = fieldMatcher.matchFields(testFields);
        Assertions.assertEquals(matchFields, Sets.newHashSet("ab"));
    }


    @Test
    public void match_x() {
        FieldMatcher fieldMatcher = new WildCardMatcher("a*");
        assertMatch_X(fieldMatcher);

    }

    @Test
    public void match_w() {
        WildCardMatcher wildCardMatcher = new WildCardMatcher("a?");
        assertMatch_W(wildCardMatcher);
    }


}