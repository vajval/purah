package org.purah.core.checker.result;

import com.google.gson.Gson;
import org.purah.core.checker.InputToCheckerArg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LogicCheckResult<T> implements CheckResult<T> {

    static String DEFAULT_SUCCESS_INFO = "SUCCESS";
    static String DEFAULT_FAILED_INFO = "FAILED";
    static String DEFAULT_ERROR_INFO = "ERROR";


    protected ExecInfo execInfo;

    protected Exception e;

    protected T data;

    protected String log;
    protected String info;

    protected LogicCheckResult(ExecInfo execInfo, T data, String log) {
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

    protected LogicCheckResult(ExecInfo execInfo, Exception e) {
        this.execInfo = execInfo;
        this.e = e;
    }

    public ExecInfo execInfo() {
        return execInfo;
    }

    public Exception getE() {
        return e;
    }

    public LogicCheckResult<T> setInfo(String info) {
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

    public static <T> LogicCheckResult<T> success(T data) {
        return new LogicCheckResult<>(ExecInfo.success, data, null);
    }
    public static <T> LogicCheckResult<T> success() {
        return new LogicCheckResult<>(ExecInfo.success, null, null);
    }

    public static <T> LogicCheckResult<T> success(T data, String log) {
        return new LogicCheckResult<>(ExecInfo.success, data, log);
    }

    public static <T> LogicCheckResult<T> failed(T data) {
        return new LogicCheckResult<>(ExecInfo.failed, data, null);
    }


    public static <T> LogicCheckResult<T> failed(T data, String log) {
        return new LogicCheckResult<>(ExecInfo.failed, data, log);
    }

    public static <T> LogicCheckResult<T> ignore(String log) {
        LogicCheckResult<T> result = new LogicCheckResult<>(ExecInfo.ignore, null, log);
        result.log = log;
        return result;

    }

    public static <T> LogicCheckResult<T> ignore() {
        return new LogicCheckResult<>(ExecInfo.ignore, null, null);

    }

    public static <T> LogicCheckResult<T> error(Exception e, String log) {
        LogicCheckResult<T> result = new LogicCheckResult<>(ExecInfo.error, e);
        result.log = log;
        return result;

    }


    protected static <A> String logStr(InputToCheckerArg<A> inputToCheckerArg, String pre) {

        String clazzStr = inputToCheckerArg.argClass().getName();

        return pre + " (field [" + inputToCheckerArg.fieldStr() + "] type [" + clazzStr + "]" + ")";
    }


    public static <A, R> LogicCheckResult<R> successBuildLog(InputToCheckerArg<A> inputToCheckerArg, R result) {
        String log = logStr(inputToCheckerArg, DEFAULT_SUCCESS_INFO);
        return LogicCheckResult.success(result, log);
    }

    public static <A, R> LogicCheckResult<R> failedBuildLog(InputToCheckerArg<A> inputToCheckerArg, R result) {
        String log = logStr(inputToCheckerArg, DEFAULT_FAILED_INFO);

        return LogicCheckResult.failed(result, log);
    }

    public static <A, R> LogicCheckResult<R> errorBuildLog(InputToCheckerArg<A> inputToCheckerArg, Exception e) {
        String log = logStr(inputToCheckerArg, DEFAULT_ERROR_INFO);
        return LogicCheckResult.error(e, log);
    }

    @Override
    public T data() {
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

        LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put("execInfo", execInfo.value());
        if (e != null) {
            objectMap.put("exception", e.getMessage());
        }
        objectMap.put("log", log);
        objectMap.put("data", data);
        return gson.toJson(objectMap);
    }

}

