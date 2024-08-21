package io.github.vajval.purah.core.checker.result;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class LogicCheckResult<T> implements CheckResult<T> {

    public static final String DEFAULT_FAILED_INFO = "FAILED";

    protected final ExecInfo execInfo;
    protected T data;

    protected String log;

    protected String info;

    protected LogicCheckResult(ExecInfo execInfo, T data, String log) {
        this.execInfo = execInfo;
        this.data = data;
        this.log = log;
        this.info = log;
        if (!StringUtils.hasText(log)) {
            this.info = execInfo.value();
        }
    }

    @Override
    public String info() {
        return info;
    }


    public ExecInfo execInfo() {
        return execInfo;
    }

    @Override
    public LogicCheckResult<T> updateInfo(String info) {
        this.info = info;
        return this;
    }

    public LogicCheckResult<T> updateInfo(InputToCheckerArg<?> inputToCheckerArg, String failedInfo) {
        Map<String, String> params = Maps.newHashMapWithExpectedSize(2);
        if (StringUtils.hasText(inputToCheckerArg.fieldPath())) {
            params.put("path", inputToCheckerArg.fieldPath());
        }
        params.put("arg", String.valueOf(inputToCheckerArg.argValue()));
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        this.info = strSubstitutor.replace(failedInfo);
        return this;
    }

    public static <T> LogicCheckResult<T> success() {
        return success(null, null);
    }

    public static <T> LogicCheckResult<T> success(T data) {
        return success(data, null);
    }

    public static <T> LogicCheckResult<T> success(T data, String log) {
        return new LogicCheckResult<>(ExecInfo.success, data, log);
    }

    public static <T> LogicCheckResult<T> failed() {
        return failed(null, null);
    }

    public static <T> LogicCheckResult<T> failedAutoInfo(InputToCheckerArg<?> inputToCheckerArg, String failedInfo) {
        LogicCheckResult<T> failed = LogicCheckResult.failed();
        failed.info = autoInfo(inputToCheckerArg, failedInfo);
        return failed;
    }

    public static <T> LogicCheckResult<T> failed(T data) {
        return failed(data, null);
    }


    public static <T> LogicCheckResult<T> failed(T data, String log) {
        return new LogicCheckResult<>(ExecInfo.failed, data, log);
    }

    public static <T> LogicCheckResult<T> ignore() {
        return ignore(null);

    }

    public static <T> LogicCheckResult<T> ignore(String log) {
        return new LogicCheckResult<>(ExecInfo.ignore, null, log);
    }

    public static String autoInfo(InputToCheckerArg<?> inputToCheckerArg, String failedInfo) {
        if (!failedInfo.contains("${")) {
            return failedInfo;
        }
        Map<String, String> params = Maps.newHashMapWithExpectedSize(2);
        if (StringUtils.hasText(inputToCheckerArg.fieldPath())) {
            params.put("path", inputToCheckerArg.fieldPath());
        }
        params.put("arg", String.valueOf(inputToCheckerArg.argValue()));
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        return strSubstitutor.replace(failedInfo);
    }

    public static <A> String logStr(InputToCheckerArg<A> inputToCheckerArg, String pre) {
        String path = inputToCheckerArg.fieldPath();
        String fieldLog = "";
        if (StringUtils.hasText(path)) {
            fieldLog = " field_path [" + inputToCheckerArg.fieldPath() + "] ";
        }
        Class<?> clazz = inputToCheckerArg.argClass();
        String clazzStr = "";
        if (!Objects.equals(clazz, Objects.class)) {
            clazzStr = "input arg type [" + clazz.getName() + "]";
        }
        String post = fieldLog + clazzStr;
        if (StringUtils.hasText(post)) {
            return pre + post;
        }
        return pre;
    }


    public static <A, R> LogicCheckResult<R> failedAutoLog(InputToCheckerArg<A> inputToCheckerArg, R result) {
        String log = logStr(inputToCheckerArg, DEFAULT_FAILED_INFO);
        return LogicCheckResult.failed(result, log);
    }


    @Override
    public T value() {
        return data;
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
        objectMap.put("log", log);
        if (!Objects.equals(log, info)) {
            objectMap.put("info", info);
        }
        objectMap.put("data", data);
        return gson.toJson(objectMap);
    }

}

