package org.purah.core.matcher;



import org.purah.core.matcher.factory.BaseMatcherFactory;
import org.purah.core.matcher.factory.MatcherFactory;
import org.purah.core.matcher.intf.FieldMatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatcherManager {

    Map<String, MatcherFactory> factoryMap = new ConcurrentHashMap<>();

    public void reg(MatcherFactory matcherFactory) {
        String name = matcherFactory.name();
        factoryMap.put(name, matcherFactory);

    }

    public void regBaseStrMatcher(Class<? extends FieldMatcher> clazz) {
        BaseMatcherFactory matcherFactory = new BaseMatcherFactory(clazz);


        this.reg(matcherFactory);


    }

    public MatcherFactory factoryOf(String factoryTypeName) {
        if (!factoryMap.containsKey(factoryTypeName)) {
            throw new RuntimeException(factoryTypeName);
        }
        return factoryMap.get(factoryTypeName);
    }


}
