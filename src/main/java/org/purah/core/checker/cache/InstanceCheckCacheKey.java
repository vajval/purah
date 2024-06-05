package org.purah.core.checker.cache;

import org.purah.core.checker.base.InputCheckArg;

import java.util.Objects;

public class InstanceCheckCacheKey {
    InputCheckArg inputCheckArg;
    String checkerName;

    public InstanceCheckCacheKey(InputCheckArg inputCheckArg, String checkerName) {
        this.inputCheckArg = inputCheckArg;
        this.checkerName = checkerName;
    }


    @Override
    public String toString() {




        return "["+ inputCheckArg.fieldStr()+"]["+ inputCheckArg.inputArg()+"]["+checkerName+"]";


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceCheckCacheKey that = (InstanceCheckCacheKey) o;
        return Objects.equals(inputCheckArg, that.inputCheckArg) && Objects.equals(checkerName, that.checkerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputCheckArg, checkerName);
    }
}
