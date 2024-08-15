package io.github.vajval.purah.spring.ioc.test_bean.matcher;

import com.google.common.collect.Sets;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.Checker;
import io.github.vajval.purah.core.checker.LambdaChecker;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.spring.IgnoreBeanOnPurahContext;
import io.github.vajval.purah.spring.ioc.ann.ToBaseMatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static io.github.vajval.purah.spring.ioc.test_bean.matcher.IocIgnoreMatcher.NAME;


@Name(NAME)
@ToBaseMatcherFactory
@IgnoreBeanOnPurahContext
public class IocIgnoreMatcher implements FieldMatcher {
    public static final String NAME = "ioc_ignore_test";



    @Override
    public Set<String> matchFields(Set<String> fields, Object inputArg) {
        return Sets.newHashSet();
    }
}
