package org.purah.core.checker.result;

import com.google.gson.Gson;
import org.purah.core.checker.InputToCheckerArg;

import java.util.HashMap;
import java.util.Map;

import static org.purah.core.checker.Checker.*;

public class BaseLogicCheckResult<T> implements CheckResult<T> {

    protected ExecInfo execInfo;

    protected Exception e;
    protected T data;

    protected String log;
    protected String info;

    protected BaseLogicCheckResult(ExecInfo execInfo, T data, String log) {
        this.execInfo = execInfo;
        this.data = data;
        this.log = log;
    }

    protected String checkLogicFrom;

    public void setCheckLogicFrom(String logicFrom) {
        this.checkLogicFrom = logicFrom;

    }

    public String checkLogicFrom() {
        return checkLogicFrom;
    }

    protected BaseLogicCheckResult(ExecInfo execInfo, Exception e) {
        this.execInfo = execInfo;
        this.e = e;
    }

    public ExecInfo execInfo() {
        return execInfo;
    }

    public Exception getE() {
        return e;
    }

    public BaseLogicCheckResult<T> setInfo(String info) {
        this.info = info;
        return this;

    }

    @Override
    public String info() {
        if (info == null) {
            return CheckResult.super.info();
        }
        return info;

    }

    public static <T> BaseLogicCheckResult<T > success() {
        return new BaseLogicCheckResult<>(ExecInfo.success, null, null);
    }

    public static <T> BaseLogicCheckResult<T> success(T data, String log) {
        return new BaseLogicCheckResult<>(ExecInfo.success, data, log);
    }

    public static <T> BaseLogicCheckResult<T> failed(T data, String log) {
        return new BaseLogicCheckResult<>(ExecInfo.failed, data, log);

    }

    public static <T> BaseLogicCheckResult<T> ignore(String log) {
        BaseLogicCheckResult<T> result = new BaseLogicCheckResult<>(ExecInfo.ignore, null, log);
        result.log = log;
        return result;

    }

    public static <T> BaseLogicCheckResult<T> error(Exception e, String log) {
        BaseLogicCheckResult<T> result = new BaseLogicCheckResult<>(ExecInfo.error, e);
        result.log = log;
        return result;

    }


    protected static <A> String logStr(InputToCheckerArg<A> inputToCheckerArg, String pre) {

        String clazzStr = inputToCheckerArg.argClass().getName();

        return pre + " (field [" + inputToCheckerArg.fieldStr() + "] type [" + clazzStr + "]" + ")";
    }


    public static <A, R> BaseLogicCheckResult<R> successBuildLog(InputToCheckerArg<A> inputToCheckerArg, R result) {
        String log = logStr(inputToCheckerArg, DEFAULT_SUCCESS_INFO);
        return BaseLogicCheckResult.success(result, log);
    }

    public static <A, R> BaseLogicCheckResult<R> failedBuildLog(InputToCheckerArg<A> inputToCheckerArg, R result) {
        String log = logStr(inputToCheckerArg, DEFAULT_FAILED_INFO);

        return BaseLogicCheckResult.failed(result, log);
    }

    public static <A, R> BaseLogicCheckResult<R> errorBuildLog(InputToCheckerArg<A> inputToCheckerArg, Exception e) {
        String log = logStr(inputToCheckerArg, DEFAULT_ERROR_INFO);
        return BaseLogicCheckResult.error(e, log);

    }
    @Override
    public T value() {
        return data;
    }

    @Override
    public Exception exception() {
        return e;
    }

    @Override
    public String log() {
        return log;
    }

    @Override
    public String toString() {


        Gson gson = new Gson();

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("execInfo", execInfo.value());
        if (e != null) {
            objectMap.put("exception", e.getMessage());
        }
        objectMap.put("log", log);
        objectMap.put("data", data);
        return gson.toJson(objectMap);
    }

}

