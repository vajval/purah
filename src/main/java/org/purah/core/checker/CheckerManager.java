package org.purah.core.checker;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.combinatorial.CombinatorialChecker;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfig;
import org.purah.core.checker.combinatorial.CombinatorialCheckerConfigProperties;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.exception.RegException;
import org.purah.core.exception.init.InitCheckerException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class CheckerManager {

    private static final Logger logger = LogManager.getLogger(CheckerManager.class);


    private final Map<String, GenericsProxyChecker> enableCheckerCacheMap = new ConcurrentHashMap<>();

    private final List<CheckerFactory> checkerFactoryList = new CopyOnWriteArrayList<>();
    private final Map<String, List<CheckerFactory>> checkerFactoryMapping = new ConcurrentHashMap<>();

    public GenericsProxyChecker reg(CombinatorialCheckerConfig config) {
        Checker<?, ?> newCombinatorialChecker = new CombinatorialChecker(config);
        return this.reg(newCombinatorialChecker);
    }


    public GenericsProxyChecker reg(Checker<?, ?> checker) {
        if (checker == null) {
            throw new RegException("checker not be null");
        }
        String name = checker.name();
        if (!StringUtils.hasText(name)) {
            throw new RegException("no name error " + name.getClass());
        }
        GenericsProxyChecker genericsProxyChecker = enableCheckerCacheMap.get(name);
        if (genericsProxyChecker == null) {
            genericsProxyChecker = GenericsProxyChecker.createAndSupportUpdateByCheckerFactory(name, 0, this::updateGenericsCheckerContext).addNewChecker(checker);
            enableCheckerCacheMap.put(name, genericsProxyChecker);

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

    public GenericsProxyChecker of(String name) {
        GenericsProxyChecker result = enableCheckerCacheMap.get(name);
        if (result == null) {
            int size = checkerFactoryList.size();
            List<CheckerFactory> enableFactoryList = searchEnableFactoryList(name, 0, size);

            if (enableFactoryList.size() == 0) {
                throw new InitCheckerException("checker no reg and cannot be matched :[  " + name + "  ]");
            }
            GenericsProxyChecker genericsProxyChecker = GenericsProxyChecker.createAndSupportUpdateByCheckerFactory(name, size, this::updateGenericsCheckerContext);

            boolean cache = false;
            //todo cache support

            for (CheckerFactory checkerFactory : enableFactoryList) {
                ProxyChecker checkerByFactory = this.createCheckerByFactory(checkerFactory, name);
                genericsProxyChecker.addNewChecker(checkerByFactory);
                logger.info("create checker:{}  by factory:{} logicFrom :{} ", name, checkerFactory.getClass(), checkerByFactory.logicFrom());

                cache = cache || checkerFactory.cacheBeCreatedChecker();

            }
            if (cache) {
                enableCheckerCacheMap.put(name, genericsProxyChecker);
            }
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
        Checker<?, ?> factoryCreatechecker = checkerFactory.createChecker(needCreateCheckerName);
//        String enableCheckerName = needCreateCheckerName;
//        if (factoryCreateChecker.name() != null) {
//            enableCheckerName = factoryCreateChecker.name();
//        }
        Class<? extends CheckerFactory> clazz = checkerFactory.getClass();
        String clazzStr = clazz.getName();
        if (clazz.isAnonymousClass()) {
            clazzStr = "anonymous class from " + clazz.getName();
        }
        String logicFrom = clazzStr + " create    " + needCreateCheckerName;
        return new ProxyChecker(factoryCreatechecker, needCreateCheckerName, logicFrom);
    }
}


