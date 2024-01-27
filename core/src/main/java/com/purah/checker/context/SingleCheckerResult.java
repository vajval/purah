package com.purah.checker.context;

public class SingleCheckerResult<T> implements CheckerResult<T> {

    protected ExecInfo execInfo;

    protected Exception e;

    protected T data;

    protected String info;
    private SingleCheckerResult(ExecInfo execInfo, T data, String info) {
        this.execInfo = execInfo;
        this.data=data;
        this.info=info;
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
        return new SingleCheckerResult<T>(ExecInfo.success, null,null);
    }

    public static <T> SingleCheckerResult<T> success(T data, String info) {
        return new SingleCheckerResult<T>(ExecInfo.success, data,info);
    }

    public static <T> SingleCheckerResult<T> failed(T data, String info) {
        return new SingleCheckerResult<T>(ExecInfo.failed, data,info);

    }

    public static <T> SingleCheckerResult<T> error(Exception e, String info) {
        SingleCheckerResult<T> result = new SingleCheckerResult<>(ExecInfo.error, e);
        result.info=info;
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
    public String info() {
        return info;
    }

    @Override
    public String toString() {
        if(e!=null){
            return "SingleCheckerResult{" +
                    "execInfo=" + execInfo +
                    ", exception=" + e +
                    ", info='" + info + '\'' +
                    '}';
        }
        return "SingleCheckerResult{" +
                "execInfo=" + execInfo +
                ", data=" + data +
                ", info='" + info + '\'' +
                '}';
    }
}

