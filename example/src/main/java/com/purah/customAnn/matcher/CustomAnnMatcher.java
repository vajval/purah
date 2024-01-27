//package com.purah.customAnn.matcher;
//
//import com.google.common.collect.Sets;
//import com.purah.base.Name;
//import com.purah.customAnn.ann.CNPhoneNum;
//import com.purah.matcher.AbstractCustomAnnMatcher;
//import com.purah.springboot.ann.EnableOnPurahContext;
//import com.purah.customAnn.ann.NotEmpty;
//import com.purah.customAnn.ann.Range;
//
//import java.lang.annotation.Annotation;
//import java.util.Set;
//
//@EnableOnPurahContext
//@Name("custom_ann")
//public class CustomAnnMatcher extends AbstractCustomAnnMatcher {
//    Set<Class<? extends Annotation>> customAnnList = Sets.newHashSet(NotEmpty.class, Range.class, CNPhoneNum.class);
//
//    public CustomAnnMatcher(String matchStr) {
//        super(matchStr);
//    }
//
//    @Override
//    public Set<Class<? extends Annotation>> customAnnList() {
//        return customAnnList;
//    }
//}
