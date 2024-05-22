package org.purah.core.checker.result;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SingleCheckerResult<T> implements CheckerResult<T> {

    protected ExecInfo execInfo;

    protected Exception e;

    protected T data;

    protected String log;

    private SingleCheckerResult(ExecInfo execInfo, T data, String log) {
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

    private SingleCheckerResult(ExecInfo execInfo, Exception e) {
        this.execInfo = execInfo;
        this.e = e;
    }

    public ExecInfo execInfo() {
        return execInfo;
    }

    public Exception getE() {
        return e;
    }


    public static <T> SingleCheckerResult<T> success() {
        return new SingleCheckerResult<T>(ExecInfo.success, null, null);
    }

    public static <T> SingleCheckerResult<T> success(T data, String log) {
        return new SingleCheckerResult<T>(ExecInfo.success, data, log);
    }

    public static <T> SingleCheckerResult<T> failed(T data, String log) {
        return new SingleCheckerResult<T>(ExecInfo.failed, data, log);

    }

    public static <T> SingleCheckerResult<T> error(Exception e, String log) {
        SingleCheckerResult<T> result = new SingleCheckerResult<>(ExecInfo.error, e);
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

