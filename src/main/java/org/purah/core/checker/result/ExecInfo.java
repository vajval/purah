package org.purah.core.checker.result;

import org.purah.core.checker.combinatorial.ExecMode;

import java.util.*;


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
