package io.github.vajval.purah.core.matcher;


import io.github.vajval.purah.core.exception.RegException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.vajval.purah.core.matcher.factory.BaseStringCacheMatcherFactory;
import io.github.vajval.purah.core.matcher.factory.MatcherFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatcherManager {
    protected static final Logger logger = LogManager.getLogger(MatcherManager.class);

    final Map<String, MatcherFactory> factoryMap = new ConcurrentHashMap<>();

    public void reg(MatcherFactory matcherFactory) {
        String name = matcherFactory.name();
        if (!StringUtils.hasText(name)) {
            throw new RegException("matcher no name "+matcherFactory.getClass());
        }
        factoryMap.put(name, matcherFactory);
    }

    public BaseStringCacheMatcherFactory regBaseStrMatcher(Class<? extends FieldMatcher> clazz) {
        BaseStringCacheMatcherFactory matcherFactory = new BaseStringCacheMatcherFactory(clazz);
        this.reg(matcherFactory);
        return matcherFactory;
    }

    public MatcherFactory factoryOf(String factoryTypeName) {
        return factoryMap.get(factoryTypeName);
    }

    public void clear() {
        factoryMap.clear();
    }


}
