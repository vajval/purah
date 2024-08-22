package io.github.vajval.purah.core.checker.result;

import io.github.vajval.purah.core.checker.InputToCheckerArg;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class LogicCheckResult<T> implements CheckResult<T> {

    public static final String DEFAULT_FAILED_INFO = "FAILED";

    protected final ExecInfo execInfo;
    protected final T data;

    protected final String log;

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
        return failedInfo
                .replace("${path}", inputToCheckerArg.fieldPath())
                .replace("${arg}", String.valueOf(inputToCheckerArg.argValue()));
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
        return "{" +
                "execInfo:'" + execInfo.value() +
                "', data:" + data +
                ", log='" + log +
                "', info='" + info +
                "'}";
    }


}

