package com.purah.checker.context;

import com.purah.checker.Checker;

public interface CheckerResult<T> {


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

    Exception exception();

    ExecInfo execInfo();

    String info();

    void setLogicFromByChecker(String logicFrom);

    String logicFrom();
}
