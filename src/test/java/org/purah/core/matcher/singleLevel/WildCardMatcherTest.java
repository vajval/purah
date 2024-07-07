package org.purah.core.matcher.singleLevel;

import com.google.common.collect.Sets;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.matcher.WildCardMatcher;

import java.util.Set;

public class WildCardMatcherTest {


    /**
     * a* ->a, ab, ac, abc, ad, af
     * a? -> ab ac ad
     * ab* -> ab,abc,abcd
     */

    static Set<String> testFields = Sets.newHashSet("a", "ab", "abc", "ac", "ad", "af", "abcd", "b");

    @Test
    public void match() {
        Assertions.assertEquals(new WildCardMatcher("a*").matchFields(testFields), Sets.newHashSet("a", "ab", "ac", "abc", "ad", "af","abcd"));
        Assertions.assertEquals(new WildCardMatcher("a?").matchFields(testFields), Sets.newHashSet("ab", "ac", "ad", "af"));
        Assertions.assertEquals(new WildCardMatcher("ab*").matchFields(testFields), Sets.newHashSet( "ab", "abc","abcd"));
        Assertions.assertEquals(new WildCardMatcher("ab*|a?").matchFields(testFields), Sets.newHashSet("ab", "ac", "ad", "af","abcd","abc"));

    }

    @Test
    public void listMatch() {

        Assertions.assertEquals(new WildCardMatcher("a*|a?").matchFields(testFields), Sets.newHashSet("a", "ab", "ac", "abc", "ad", "af","abcd"));
        Assertions.assertEquals(new WildCardMatcher("a?").matchFields(testFields), Sets.newHashSet("ab", "ac", "ad", "af"));


    }

}

