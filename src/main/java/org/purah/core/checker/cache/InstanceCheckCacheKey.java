package org.purah.core.checker.cache;

import org.purah.core.checker.base.CheckInstance;
import org.purah.core.checker.base.Checker;

import java.util.Objects;

public class InstanceCheckCacheKey {
    CheckInstance checkInstance;
    String checkerName;

    public InstanceCheckCacheKey(CheckInstance checkInstance, String checkerName) {
        this.checkInstance = checkInstance;
        this.checkerName = checkerName;
    }


    @Override
    public String toString() {




        return "["+checkInstance.fieldStr()+"]["+checkInstance.instance()+"]["+checkerName+"]";


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceCheckCacheKey that = (InstanceCheckCacheKey) o;
        return Objects.equals(checkInstance, that.checkInstance) && Objects.equals(checkerName, that.checkerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkInstance, checkerName);
    }
}
