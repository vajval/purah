package com.purah.checker.context;

public interface CheckerResult<T> {
    T value();

    default boolean success() {
        return execInfo() == ExecInfo.success;
    }

    default boolean failed() {
        return execInfo() == ExecInfo.failed;
    }

    default boolean error() {
        return execInfo() == ExecInfo.error;
    }

    Exception exception();

    ExecInfo execInfo();

}
