package org.purah.core.matcher;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.matcher.factory.BaseMatcherFactory;
import org.purah.core.matcher.factory.MatcherFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatcherManager {
    protected static final Logger logger = LogManager.getLogger(MatcherManager.class);

    final Map<String, MatcherFactory> factoryMap = new ConcurrentHashMap<>();

    public void reg(MatcherFactory matcherFactory) {
        String name = matcherFactory.name();
        factoryMap.put(name, matcherFactory);


    }

    public BaseMatcherFactory regBaseStrMatcher(Class<? extends FieldMatcher> clazz) {
        BaseMatcherFactory matcherFactory = new BaseMatcherFactory(clazz);
        this.reg(matcherFactory);
        return matcherFactory;


    }

    public MatcherFactory factoryOf(String factoryTypeName) {
        if (!factoryMap.containsKey(factoryTypeName)) {
            logger.error("no reg MatcherFactory: {}", factoryTypeName);
            throw new RuntimeException("no reg MatcherFactory: " + factoryTypeName);
        }
        return factoryMap.get(factoryTypeName);
    }


}
