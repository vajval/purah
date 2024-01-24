package com.purah.checker;


import com.purah.checker.factory.CheckerFactory;
import com.purah.exception.RuleRegException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则的管理器
 * 最好先注册字段规则
 * 再处理instance 规则
 */
public class CheckerManager {

    protected final Map<String, ExecChecker> cacheMap = new ConcurrentHashMap<>();


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
                    CheckerProxy checkerProxy = new CheckerProxy(factoryCreatechecker) {
                        @Override
                        public String name() {
                            return name;
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


