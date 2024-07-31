package io.github.vajval.purah.spring.ioc.test_bean.matcher;

public class PurahUtils {
    public static class Match {
        public static String reverse=ReverseStringMatcher.NAME;
    }
    public static void main(String[] args) {
        String reverse = PurahUtils.Match.reverse;
    }
}