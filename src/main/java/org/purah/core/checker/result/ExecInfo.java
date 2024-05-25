package org.purah.core.checker.result;

public enum ExecInfo {
    ignore("IGNORE"),
    success("SUCCESS"),
    failed("FAILED"),
    error("ERROR");
    String value;

    ExecInfo(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
