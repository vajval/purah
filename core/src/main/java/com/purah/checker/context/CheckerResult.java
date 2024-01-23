package com.purah.checker.context;

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

}
