package com.purah.matcher.singleLevel;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class WildCardMatcherTest {


    @Test
    public void match_x() {
        Set<String> testFields = Sets.newHashSet("a", "ab", "abc", "abcd","ba");
        WildCardMatcher wildCardMatcher = new WildCardMatcher("a*");
        Set<String> matchFields = wildCardMatcher.matchFields(testFields);
        Assertions.assertEquals(matchFields, Sets.newHashSet("a", "ab","abc", "abcd"));
    }

    @Test
    public void match_w() {
        Set<String> testFields = Sets.newHashSet("a", "ab", "abc", "abcd","ba");
        WildCardMatcher wildCardMatcher = new WildCardMatcher("a?");
        Set<String> matchFields = wildCardMatcher.matchFields(testFields);
        Assertions.assertEquals(matchFields, Sets.newHashSet( "ab"));
    }
}