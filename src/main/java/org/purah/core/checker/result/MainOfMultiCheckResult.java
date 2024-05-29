package org.purah.core.checker.result;

public class MainOfMultiCheckResult<T>  extends BaseLogicCheckResult<T>{
    public MainOfMultiCheckResult(ExecInfo execInfo, T data, String log) {

        super(execInfo, data, log);
    }

    public MainOfMultiCheckResult(ExecInfo execInfo, Exception e) {
        
        super(execInfo, e);
    }

    public static <T> MainOfMultiCheckResult<T> success() {
        return new MainOfMultiCheckResult<T>(ExecInfo.success, null, null);
    }

    public static <T> MainOfMultiCheckResult<T> success(T data, String log) {
        return new MainOfMultiCheckResult<T>(ExecInfo.success, data, log);
    }

    public static <T> MainOfMultiCheckResult<T> failed(T data, String log) {
        return new MainOfMultiCheckResult<T>(ExecInfo.failed, data, log);

    }

    public static <T> MainOfMultiCheckResult<T> ignore(String log) {
        MainOfMultiCheckResult<T> result = new MainOfMultiCheckResult<>(ExecInfo.ignore, null, log);
        result.log = log;
        return result;

    }

    public static <T> MainOfMultiCheckResult<T> error(Exception e, String log) {
        MainOfMultiCheckResult<T> result = new MainOfMultiCheckResult<>(ExecInfo.error, e);
        result.log = log;
        return result;

    }
}
