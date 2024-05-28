package org.purah.core.checker;


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

    public final Map<String, ExecChecker> cacheMap = new ConcurrentHashMap<>();


    public List<CheckerFactory> checkerFactoryList = new ArrayList<>();


    public ExecChecker<?, ?> reg(Checker<?, ?> checker) {
        if (checker == null) {
            throw new RuleRegException("注册规则不能为空");
        }
        String name = checker.name();
        ExecChecker<?, ?> execChecker = cacheMap.get(name);
        if (execChecker == null) {
            execChecker = new ExecChecker<>(name);
            cacheMap.put(name, execChecker);
        }
        execChecker.addNewChecker(checker);
        return execChecker;
    }

    public void addCheckerFactory(CheckerFactory checkerFactory) {
        this.checkerFactoryList.add(checkerFactory);

    }

    public ExecChecker<?, ?> get(String name) {
        ExecChecker result = cacheMap.get(name);

        if (result == null) {
            for (CheckerFactory checkerFactory : checkerFactoryList) {

                if (checkerFactory.match(name)) {

                    Checker factoryCreatechecker = checkerFactory.createChecker(name);

                    String factoryCreateCheckerName = factoryCreatechecker.name();

                    CheckerProxy checkerProxy = new CheckerProxy(factoryCreatechecker) {
                        @Override
                        public String name() {
                            if (factoryCreateCheckerName != null) {
                                return factoryCreateCheckerName;
                            }
                            return name;
                        }

                        @Override
                        public String logicFrom() {
                            Class<? extends CheckerFactory> clazz = checkerFactory.getClass();
                            String clazzStr = clazz.getName();
                            if (clazz.isAnonymousClass()) {
                                clazzStr = "anonymous class from " + clazz.getName();
                            }
                            return clazzStr+" create    "+ name ;
                        }
                    };

                    return this.reg(checkerProxy);

                }
            }


            throw new RuleRegException("未经注册的规则:" + name);
        }


        return result;
    }


}


