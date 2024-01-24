package com.purah.resolver;

import com.purah.checker.CheckInstance;
import com.purah.checker.Checker;
import org.springframework.core.ResolvableType;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MapStringObjectArgResolver extends AbstractMatchArgResolver<Map<String, Object>> {


    @Override
    public Map<String, CheckInstance> getFieldsObjectMap(Map<String, Object> stringObjectMap, Set<String> matchFieldList) {
        return matchFieldList.stream().collect(Collectors.toMap(matchField -> matchField, i -> CheckInstance.create(stringObjectMap.get(i))));
    }

    @Override
    protected Set<String> fields(Map<String, Object> stringObjectMap) {
        return stringObjectMap.keySet();
    }

    @Override
    public boolean support(Class<?> clazz) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }


        ResolvableType[] generics = ResolvableType
                .forClass(clazz)
                .as(Map.class)
                .getGenerics();


        return String.class.isAssignableFrom((Objects.requireNonNull(generics[0].resolve())));

    }
}
