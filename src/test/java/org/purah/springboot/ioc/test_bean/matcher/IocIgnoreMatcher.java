package org.purah.springboot.ioc.test_bean.matcher;

import com.google.common.collect.Sets;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.name.Name;
import org.purah.springboot.IgnoreBeanOnPurahContext;
import org.purah.springboot.ioc.ann.ToBaseMatcherFactory;

import java.util.Set;

import static org.purah.springboot.ioc.test_bean.matcher.IocIgnoreMatcher.NAME;


@Name(NAME)
@ToBaseMatcherFactory
@IgnoreBeanOnPurahContext
public class IocIgnoreMatcher implements FieldMatcher {
    public static final String NAME = "ioc_ignore_test";
    @Override
    public boolean supportCache() {
        return FieldMatcher.super.supportCache();
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object inputArg) {
        return Sets.newHashSet();
    }
}
