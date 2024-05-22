package org.purah.springboot.result;

import org.purah.core.checker.result.CheckerResult;
import org.purah.core.checker.result.CombinatorialCheckerResult;
import org.purah.core.checker.result.ExecInfo;

import java.util.List;

public class ArgCheckResult implements CheckerResult<List<CombinatorialCheckerResult>> {


    @Override
    public List<CombinatorialCheckerResult> value() {
        return null;
    }


    @Override
    public Exception exception() {
        return null;
    }

    @Override
    public ExecInfo execInfo() {
        return null;
    }

    @Override
    public String log() {
        return null;
    }

    @Override
    public void setCheckLogicFrom(String logicFrom) {

    }

    @Override
    public String checkLogicFrom() {
        return null;
    }
}
