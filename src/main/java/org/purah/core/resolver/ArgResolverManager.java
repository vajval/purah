package org.purah.core.resolver;


import org.checkerframework.checker.units.qual.A;
import org.purah.core.base.NameUtil;
import org.purah.core.exception.RegException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 解析器 管理器
 */
public class ArgResolverManager {

    //反射解析器 当没有其他可用的的解析器时 使用这个
    ArgResolver defaultResolver = new DefaultArgResolver();
    List<ArgResolver> argResolverList = new ArrayList<>();

    public ArgResolver getArgResolver(Class<?> argClass) {
        return defaultResolver;
    }

    public void reg(ArgResolver argResolver) {
        argResolverList.add(argResolver);
    }


}
