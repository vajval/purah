package io.github.vajval.purah.core.checker.result;

import io.github.vajval.purah.core.checker.combinatorial.ExecMode;


public enum ExecInfo {
    /**
     * @see ExecMode.Main
     */
    ignore("IGNORE"),
    success("SUCCESS"),
    failed("FAILED"),
    error("ERROR");

    private final String value;

    ExecInfo(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
