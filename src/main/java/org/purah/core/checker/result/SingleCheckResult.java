package org.purah.core.checker.result;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SingleCheckResult<T> implements CheckResult<T> {

    protected ExecInfo execInfo;

    protected Exception e;

    protected T data;

    protected String log;

    private SingleCheckResult(ExecInfo execInfo, T data, String log) {
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

    private SingleCheckResult(ExecInfo execInfo, Exception e) {
        this.execInfo = execInfo;
        this.e = e;
    }

    public ExecInfo execInfo() {
        return execInfo;
    }

    public Exception getE() {
        return e;
    }


    public static <T> SingleCheckResult<T> success() {
        return new SingleCheckResult<T>(ExecInfo.success, null, null);
    }

    public static <T> SingleCheckResult<T> success(T data, String log) {
        return new SingleCheckResult<T>(ExecInfo.success, data, log);
    }

    public static <T> SingleCheckResult<T> failed(T data, String log) {
        return new SingleCheckResult<T>(ExecInfo.failed, data, log);

    }
    public static <T> SingleCheckResult<T> ignore(String log) {
        SingleCheckResult<T> result = new SingleCheckResult<>(ExecInfo.ignore, null,log);
        result.log = log;
        return result;

    }
    public static <T> SingleCheckResult<T> error(Exception e, String log) {
        SingleCheckResult<T> result = new SingleCheckResult<>(ExecInfo.error, e);
        result.log = log;
        return result;

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

//    @Override
//    public boolean isMatchedResult() {
//        return false;
//    }
}

