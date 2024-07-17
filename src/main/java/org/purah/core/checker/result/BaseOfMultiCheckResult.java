package org.purah.core.checker.result;

public class BaseOfMultiCheckResult<T>  extends LogicCheckResult<T> {
    public BaseOfMultiCheckResult(ExecInfo execInfo, T data, String log) {

        super(execInfo, data, log);
    }

    public BaseOfMultiCheckResult(ExecInfo execInfo, Exception e) {
        
        super(execInfo, e);
    }

    public static <T> BaseOfMultiCheckResult<T> success() {
        return new BaseOfMultiCheckResult<>(ExecInfo.success, null, null);
    }

    public static <T> BaseOfMultiCheckResult<T> success(T data, String log) {
        return new BaseOfMultiCheckResult<>(ExecInfo.success, data, log);
    }

    public static <T> BaseOfMultiCheckResult<T> failed(T data, String log) {
        return new BaseOfMultiCheckResult<>(ExecInfo.failed, data, log);

    }

    public static <T> BaseOfMultiCheckResult<T> ignore() {
        return new BaseOfMultiCheckResult<>(ExecInfo.ignore,null,null);

    }
    public static <T> BaseOfMultiCheckResult<T> ignore(String log) {
        return new BaseOfMultiCheckResult<>(ExecInfo.ignore,null,log);

    }
    public static <T> BaseOfMultiCheckResult<T> error(Exception e, String log) {
        BaseOfMultiCheckResult<T> result = new BaseOfMultiCheckResult<>(ExecInfo.error, e);
        result.log = log;
        return result;

    }
}
