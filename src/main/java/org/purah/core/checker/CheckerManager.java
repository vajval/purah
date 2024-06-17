package org.purah.core.checker;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.exception.CheckerRegException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 规则的管理器
 * 最好先注册字段规则
 * 再处理instance 规则
 */
public class CheckerManager {

    private static final Logger logger = LogManager.getLogger(CheckerManager.class);

    /**
     * 根据名字匹配规则
     * 根据入参class选择使用的checker 在GenericsProxyChecker内部实现
     */

    private final Map<String, GenericsProxyChecker> cacheMap = new ConcurrentHashMap<>();

    private final List<CheckerFactory> checkerFactoryList = new CopyOnWriteArrayList<>();
    private final Map<String, List<CheckerFactory>> checkerFactoryMapping = new ConcurrentHashMap<>();

    public GenericsProxyChecker reg(Checker<?, ?> checker) {
        if (checker == null) {
            throw new CheckerRegException("注册规则不能为空");
        }
        String name = checker.name();
        if (!StringUtils.hasText(name)) {
            throw new CheckerRegException(name.getClass() + "no name");
        }
        GenericsProxyChecker genericsProxyChecker = cacheMap.get(name);
        if (genericsProxyChecker == null) {
            genericsProxyChecker = GenericsProxyChecker.createAndSupportUpdateByCheckerFactory(name, 0, this::updateGenericsCheckerContext).addNewChecker(checker);
            cacheMap.put(name, genericsProxyChecker);

            return genericsProxyChecker;
        }
        genericsProxyChecker.addNewChecker(checker);

        return genericsProxyChecker;
    }

    public void addCheckerFactory(CheckerFactory checkerFactory) {
        this.checkerFactoryList.add(checkerFactory);
        String searchKey = getSearchKey(checkerFactory.name());
        if (searchKey == null) {
            return;
        }

        checkerFactoryMapping.computeIfAbsent(searchKey, i -> {
            CopyOnWriteArrayList<CheckerFactory> list = new CopyOnWriteArrayList<>();
            list.add(checkerFactory);
            return list;
        });
    }

    protected int updateGenericsCheckerContext(GenericsProxyChecker genericsProxyChecker, int factoryOldCount) {


        if (factoryOldCount == checkerFactoryList.size()) {
            return checkerFactoryList.size();
        }
        String needMatchCheckerName = genericsProxyChecker.name;
        List<CheckerFactory> updateCheckerFactoryList = searchEnableFactoryList(needMatchCheckerName, factoryOldCount, checkerFactoryList.size());
        List<ProxyChecker> newProxyChecker = updateCheckerFactoryList.stream().map(i -> createCheckerByFactory(i, needMatchCheckerName)).collect(Collectors.toList());
        for (ProxyChecker proxyChecker : newProxyChecker) {
            genericsProxyChecker.addNewChecker(proxyChecker);
        }
        return checkerFactoryList.size();
    }

    public GenericsProxyChecker get(String name) {
        GenericsProxyChecker result = cacheMap.get(name);
        if (result == null) {
            List<Checker<?, ?>> checkers = new ArrayList<>();
            int size = checkerFactoryList.size();
            List<CheckerFactory> enableFactoryList = searchEnableFactoryList(name, 0, size);
            for (CheckerFactory checkerFactory : enableFactoryList) {
                ProxyChecker checkerByFactory = this.createCheckerByFactory(checkerFactory, name);
                checkers.add(checkerByFactory);
                logger.info("create checker:{}  by factory:{} logicFrom :{} ", name, checkerFactory.getClass(), checkerByFactory.logicFrom());

            }
            if (checkers.size() == 0) {
                throw new CheckerRegException("未经注册也无法匹配的规则:" + name);
            }
            GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createAndSupportUpdateByCheckerFactory(name, size, this::updateGenericsCheckerContext);

            for (Checker<?, ?> checker : checkers) {
                genericsProxyChecker.addNewChecker(checker);
            }
            cacheMap.put(name, genericsProxyChecker);
            return genericsProxyChecker;
        } else {
            return result;
        }
    }


    protected List<CheckerFactory> searchEnableFactoryList(String name, int begin, int end) {
        String searchKey = getSearchKey(name);


        List<CheckerFactory> searchList = checkerFactoryMapping.get(searchKey);
        if (searchList != null) {
            return searchList.stream().filter(i -> i.match(name)).collect(Collectors.toList());
        }

        List<CheckerFactory> result = new ArrayList<>();
        for (int index = begin; index < end; index++) {
            CheckerFactory checkerFactory = checkerFactoryList.get(index);
            if (checkerFactory.match(name)) {
                result.add(checkerFactory);
            }
        }
        return result;
    }

    protected String getSearchKey(String name) {
        return name;
    }

    protected ProxyChecker createCheckerByFactory(CheckerFactory checkerFactory, String needCreateCheckerName) {
        Checker factoryCreatechecker = checkerFactory.createChecker(needCreateCheckerName);
        String enableCheckerName = needCreateCheckerName;
//        if (factoryCreatechecker.name() != null) {
//            enableCheckerName = factoryCreatechecker.name();
//        }
        Class<? extends CheckerFactory> clazz = checkerFactory.getClass();
        String clazzStr = clazz.getName();
        if (clazz.isAnonymousClass()) {
            clazzStr = "anonymous class from " + clazz.getName();
        }
        String logicFrom = clazzStr + " create    " + needCreateCheckerName;
        return new ProxyChecker(factoryCreatechecker, enableCheckerName, logicFrom);
    }
}


