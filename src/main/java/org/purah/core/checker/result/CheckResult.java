package org.purah.core.checker.result;

import java.util.function.BooleanSupplier;

public interface CheckResult<T> extends BooleanSupplier {


    T data();

    default boolean isSuccess() {
        return execInfo() == ExecInfo.success;
    }

    @Override
    default boolean getAsBoolean() {
        return isSuccess();
    }

//    default boolean isFromCache() {
//        return false;
//    }

    default boolean isFailed() {
        return execInfo() == ExecInfo.failed;
    }

    default boolean isError() {
        return execInfo() == ExecInfo.error;
    }

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
