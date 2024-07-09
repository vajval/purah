package org.purah.core.checker.cache;

import org.purah.core.checker.InputToCheckerArg;

import java.util.Objects;

/**
 * cache key
 */
public class InputToCheckerArgCacheKey {
    InputToCheckerArg inputToCheckerArg;
    String checkerName;

    public InputToCheckerArgCacheKey(InputToCheckerArg inputToCheckerArg, String checkerName) {
        this.inputToCheckerArg = inputToCheckerArg;
        this.checkerName = checkerName;
    }


    @Override
    public String toString() {
        if (inputToCheckerArg == null) {
            return "[#NULL#][" + checkerName + "]";
        }
        return "[" + inputToCheckerArg.fieldStr() + "][" + inputToCheckerArg.argValue() + "][" + checkerName + "]";


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputToCheckerArgCacheKey that = (InputToCheckerArgCacheKey) o;
        return Objects.equals(inputToCheckerArg, that.inputToCheckerArg) && Objects.equals(checkerName, that.checkerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputToCheckerArg, checkerName);
    }
}