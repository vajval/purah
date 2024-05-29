package org.purah.core.checker.result;

public interface CheckResult<T> {


    T value();

    default boolean isSuccess() {
        return execInfo() == ExecInfo.success;
    }

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
