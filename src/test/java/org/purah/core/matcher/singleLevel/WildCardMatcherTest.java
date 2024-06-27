package org.purah.core.matcher.singleLevel;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.WildCardMatcher;

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
    public void matc() {
        FieldMatcher fieldMatcher = new WildCardMatcher("a?|abc?");
        Set<String> matchFields = fieldMatcher.matchFields(testFields);
        Assertions.assertEquals(matchFields, Sets.newHashSet("ab", "abcd"));
    }

    @Test
    public void match_x() {
        FieldMatcher fieldMatcher = new WildCardMatcher("a*");
        Set<String> matchFields = fieldMatcher.matchFields(testFields);
        Assertions.assertEquals(matchFields, Sets.newHashSet("a", "ab", "abc", "abcd"));

    }

    @Test
    public void match_w() {
        WildCardMatcher wildCardMatcher = new WildCardMatcher("a?");
        Set<String> matchFields = wildCardMatcher.matchFields(testFields);
        Assertions.assertEquals(matchFields, Sets.newHashSet("ab"));
    }


}