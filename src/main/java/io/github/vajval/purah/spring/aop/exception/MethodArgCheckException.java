package io.github.vajval.purah.spring.aop.exception;


import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.exception.BasePurahException;
import io.github.vajval.purah.spring.aop.result.MethodHandlerCheckResult;

import java.util.List;

public class MethodArgCheckException extends BasePurahException {


    final MethodHandlerCheckResult checkResult;

    public MethodArgCheckException(MethodHandlerCheckResult checkResult) {

        super(checkResult.execInfo().name());
        this.checkResult = checkResult;

    }

    public MethodHandlerCheckResult checkResult() {
        return checkResult;
    }

    public List<LogicCheckResult<?>> failedLogicList() {
        return checkResult.failedLogicList();
    }

    public List<LogicCheckResult<?>> failedList() {
        return checkResult.childList(ResultLevel.only_failed);
    }

    public List<LogicCheckResult<?>> errorList() {
        return checkResult.childList(ResultLevel.only_error);
    }
}

