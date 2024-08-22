package io.github.vajval.purah.spring.ioc.ann;

import io.github.vajval.purah.ExampleApplication;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = ExampleApplication.class)
public class ToCheckerTest {

    @Autowired
    Purahs purahs;

    @BeforeEach
    public void beforeEach() {

    }

    @Test
    public void toChecker() {
        User user = new User(1L, null, "123435345", 123);
        MultiCheckResult<?> multiCheckResult =
                (MultiCheckResult) purahs.checkerOf("example:1[][id|phone|name:not_null_test]").oCheck(user);
        List<LogicCheckResult<?>> logicCheckResults = multiCheckResult.resultChildList(ResultLevel.only_failed_only_base_logic);
        LogicCheckResult<?> checkResult = logicCheckResults.get(0);
        Assertions.assertTrue(checkResult.info().contains("失败 字段 [name] 值为[null]"));


    }

}
