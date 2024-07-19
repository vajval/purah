package org.purah.util;

import org.purah.core.name.Name;
import org.purah.core.checker.AbstractCustomAnnChecker;
import org.purah.core.checker.Checker;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.converter.checker.ByAnnMethodChecker;
import org.purah.core.checker.result.ResultLevel;
import org.purah.example.customAnn.ann.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
@Name("测试注解检测")
@Component
public class TestAnnChecker  extends AbstractCustomAnnChecker
{

    public TestAnnChecker() {
        super(ExecMode.Main.all_success, ResultLevel.failedAndIgnoreNotBaseLogic);
    }


    public boolean notNull(NotNull notNull, Integer age) {
        return age != null;


    }
    @Override
    public Checker methodToChecker(Object methodsToCheckersBean, Method method, String name) {
        return new ByAnnMethodChecker(methodsToCheckersBean, method, name);
    }
}
