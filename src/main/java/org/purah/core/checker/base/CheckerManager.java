package org.purah.core.checker.base;


import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.exception.RuleRegException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则的管理器
 * 最好先注册字段规则
 * 再处理instance 规则
 */
public class CheckerManager {


    /**
     * 根据名字匹配规则
     * 根据入参class选择使用的checker 在ExecChecker内部实现
     */

    private final Map<String, GenericsProxyChecker> cacheMap = new ConcurrentHashMap<>();

    private final List<CheckerFactory> checkerFactoryList = new ArrayList<>();


    public GenericsProxyChecker<?, ?> reg(Checker<?, ?> checker) {
        if (checker == null) {
            throw new RuleRegException("注册规则不能为空");
        }
        String name = checker.name();
        if (name == null) {
            throw new RuntimeException();
        }
        GenericsProxyChecker<?, ?> genericsProxyChecker = cacheMap.get(name);
        if (genericsProxyChecker == null) {
            genericsProxyChecker = new GenericsProxyChecker<>(name, checker);
            cacheMap.put(name, genericsProxyChecker);
        } else {
            genericsProxyChecker.addNewChecker(checker);

        }
        return genericsProxyChecker;
    }

    public void addCheckerFactory(CheckerFactory checkerFactory) {
        this.checkerFactoryList.add(checkerFactory);
    }



    public GenericsProxyChecker<?, ?> get(String name) {
        GenericsProxyChecker<?, ?> result = cacheMap.get(name);
        if (result == null) {
            for (CheckerFactory checkerFactory : checkerFactoryList) {
                if (checkerFactory.match(name)) {
                    CheckerProxy checkerByFactory = this.createCheckerByFactory(checkerFactory, name);
                    if (checkerFactory.cacheBeCreatedChecker()) {
                        return this.reg(checkerByFactory);
                    }
                    return new GenericsProxyChecker<>(name, checkerByFactory);
                }
            }
            throw new RuleRegException("未经注册也无法匹配的规则:" + name);
        }
        return result;
    }

    protected CheckerProxy createCheckerByFactory(CheckerFactory checkerFactory, String needCreateCheckerName) {
        Checker factoryCreatechecker = checkerFactory.createChecker(needCreateCheckerName);
        String enableCheckerName = needCreateCheckerName;
        if (factoryCreatechecker.name() != null) {
            enableCheckerName = factoryCreatechecker.name();
        }

        Class<? extends CheckerFactory> clazz = checkerFactory.getClass();
        String clazzStr = clazz.getName();
        if (clazz.isAnonymousClass()) {
            clazzStr = "anonymous class from " + clazz.getName();
        }
        String logicFrom = clazzStr + " create    " + needCreateCheckerName;

        return new CheckerProxy(factoryCreatechecker, enableCheckerName, logicFrom);
    }
}


