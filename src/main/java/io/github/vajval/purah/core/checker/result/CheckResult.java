package io.github.vajval.purah.core.checker.result;

import io.github.vajval.purah.core.exception.CheckException;

import java.util.function.BooleanSupplier;


public interface CheckResult<T> extends BooleanSupplier {


    T data();

    default boolean isSuccess() {
        if (isIgnore()) {
            throw new RuntimeException("This check has been ignored,It is not certain whether it will succeed or fail. ");
        }
        return execInfo() == ExecInfo.success;
    }

    @Override
    default boolean getAsBoolean() {
        return isSuccess();
    }

    //todo isFromCache

//    default boolean isFromCache() {
//        return false;
//    }

    default boolean isFailed() {
        if (isIgnore()) {
            throw new RuntimeException("This check has been ignored,It is not certain whether it will succeed or fail. ");
        }
        return execInfo() == ExecInfo.failed;
    }

//    default boolean isError() {
//        return execInfo() == ExecInfo.error;
//    }

    default boolean isIgnore() {
        return execInfo() == ExecInfo.ignore;
    }

    default String info() {
        return log();
    }

    Exception exception();

    ExecInfo execInfo();

    String log();

    void setCheckLogicFrom(String logicFrom);

    String checkLogicFrom();
}
