package org.purah.core.checker.result;

public class MainOfMultiCheckResult<T>  extends BaseLogicCheckResult<T>{
    public MainOfMultiCheckResult(ExecInfo execInfo, T data, String log) {

        super(execInfo, data, log);
    }

    public MainOfMultiCheckResult(ExecInfo execInfo, Exception e) {
        
        super(execInfo, e);
    }

    public static <T> MainOfMultiCheckResult<T> success() {
        return new MainOfMultiCheckResult<>(ExecInfo.success, null, null);
    }

    public static <T> MainOfMultiCheckResult<T> success(T data, String log) {
        return new MainOfMultiCheckResult<>(ExecInfo.success, data, log);
    }

    public static <T> MainOfMultiCheckResult<T> failed(T data, String log) {
        return new MainOfMultiCheckResult<>(ExecInfo.failed, data, log);

    }



    public static <T> MainOfMultiCheckResult<T> error(Exception e, String log) {
        MainOfMultiCheckResult<T> result = new MainOfMultiCheckResult<>(ExecInfo.error, e);
        result.log = log;
        return result;

    }
}
