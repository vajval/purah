package com.purah.checker;

import com.purah.checker.context.SingleCheckerResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExecChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {

    public Map<Class<?>, Checker> cacheMap = new ConcurrentHashMap<>();


    public String name;

    public void addChecker(Checker<?, ?> checker) {
        cacheMap.put(checker.inputCheckInstanceClass(), checker);

    }

    public ExecChecker(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }


    @Override
    public SingleCheckerResult<RESULT> check(CheckInstance<CHECK_INSTANCE> checkInstance) {
        if (checkInstance == null) {
            return cacheMap.values().iterator().next().check(null);
        }
        Class<?> instanceClazz = checkInstance.instance().getClass();
        Checker checker = cacheMap.get(instanceClazz);
        if (checker == null) {
            throw new RuntimeException("没有合适的检查器");
        }
        return checker.check(checkInstance);
    }
}

