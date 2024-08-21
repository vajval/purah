package io.github.vajval.purah.core.checker.result;

import java.util.function.BooleanSupplier;


public interface CheckResult<T> extends BooleanSupplier {


    T data();



    @Override
    default boolean getAsBoolean() {
        return isSuccess();
    }

    default String info() {
        return log();
    }

    ExecInfo execInfo();

    String log();

    void setCheckerLogicFrom(String logicFrom);

    String checkLogicFrom();

    CheckResult<T> setInfo(String info);



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
    default boolean isSuccess() {
        if (isIgnore()) {
            throw new RuntimeException("This check has been ignored,It is not certain whether it will succeed or fail. ");
        }
        return execInfo() == ExecInfo.success;
    }

    default boolean isIgnore() {
        return execInfo() == ExecInfo.ignore;
    }


}
