package org.purah.core.matcher.nested;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.singlelevel.WildCardMatcher;
import org.purah.core.matcher.inft.ListIndexMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;


/*
 * 获取所有符合 needBeCollected ()的field的值
 * 输入 needNestedPackagePatch , 如果Field的package 符合needNestedPackagePatch
 * 就对这个Field 进行嵌套匹配
 *
 * <p>
 * package my.company.project
 * <p>
 * People{
 *    @TestAnn
 *    People child;
 *
 *    @TestAnn
 *    String name;
 *
 *    String id;
 * }
 * <p>
 * new People(name:1,id:11 ,child:new People(name:2,id:22,child:null))
 * new AnnByPackageMatcher("my.company.*")  ---------      needBeCollected(field)->field.hasAnn(@TestAnn.class)
 * <p>
 * return {name:11,child:"name:2,id:22,child:null",child.name:22,child.child:null}
 *
 *  The input parameter represents the type that requires nested checks.
 *  If the class of the field is within the specified package path,
 *  then the value of this field will undergo the same matching process.
 *
 */
//todo  list nested
//todo  circular dependency error

public class AnnByPackageMatcher extends BaseStringMatcher implements MultilevelFieldMatcher, ListIndexMatcher {

    protected WildCardMatcher fieldNeedNestedMatcher;

    protected Map<Class<?>, Map<String, FieldInfoCache>> fieldCache = new ConcurrentHashMap<>();

    protected FieldInfoCache NULL = new FieldInfoCache();

    protected int maxDepth;

    protected Set<Class<? extends Annotation>> annClazz;


    public AnnByPackageMatcher(String needNestedPackagePatch) {
        this(needNestedPackagePatch, 8);
    }


    @SafeVarargs
    public AnnByPackageMatcher(String needNestedPackagePatch, int maxDepth, Class<? extends Annotation>... annClazz) {
        super(needNestedPackagePatch);
        this.fieldNeedNestedMatcher = new WildCardMatcher(needNestedPackagePatch);
        this.maxDepth = maxDepth;

        for (Class<? extends Annotation> clazz : annClazz) {
            if (!Annotation.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("clazz must be ann Clazz");
            }

        }
        this.annClazz = Sets.newHashSet(annClazz);
    }


    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Map<String, FieldInfoCache> fieldInfoCacheMap = fieldInfoCacheMap(belongInstance.getClass());
        Set<String> result = Sets.newHashSet();
        for (String field : fields) {
            FieldInfoCache fieldInfoCache = fieldInfoCacheMap.get(field);
            if (fieldInfoCache.needBeCollected || fieldInfoCache.needNest) {
                result.add(field);
            }
        }
        return result;
    }


    @Override
    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        FieldInfoCache fieldInfoCache = fieldInfoCache(matchedField, inputArg.argClass());
        boolean needNest = fieldInfoCache.needNest;
        boolean needBeCollected = fieldInfoCache.needBeCollected;

        if (fieldInfoCache == NULL) {
            needNest = fieldNeedNestedMatcher.match(childArg.argClass().getPackage().getName());
            needBeCollected = false;
        }

        if (this.maxDepth == 0) {
            needNest = false;
        }

        if (needBeCollected) {
            if (needNest) {
                return NestedMatchInfo.needCollectedAndMatchNested(childFieldMatcher());
            } else {
                return NestedMatchInfo.needCollected();
            }
        } else {
            if (needNest) {
                return NestedMatchInfo.justNested(childFieldMatcher());
            } else {
                return NestedMatchInfo.ignore();
            }
        }

    }

    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        if (CollectionUtils.isEmpty(objectList)) return new HashMap<>();
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(objectList.size());
        for (int index = 0; index < objectList.size(); index++) {
            Object o = objectList.get(index);
            if (o != null && fieldNeedNestedMatcher.match(o.getClass().getPackage().getName())) {
                result.put("#" + index, o);
            }
        }
        return result;

    }


    protected boolean needBeCollected(Field field) {
        for (Class<? extends Annotation> clazz : annClazz) {
            if (field.getDeclaredAnnotation(clazz) != null) {
                return true;
            }
        }
        return false;
    }

    protected FieldMatcher childFieldMatcher() {
        Predicate<Field> predicate = this::needBeCollected;
        return new AnnByPackageMatcher(this.matchStr, this.maxDepth - 1) {
            @Override
            protected boolean needBeCollected(Field field) {
                return predicate.test(field);
            }
        };
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnnByPackageMatcher that = (AnnByPackageMatcher) o;
        return maxDepth == that.maxDepth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxDepth);
    }

    @Override
    public boolean supportCache() {
        return !matchStr.contains("#");

    }

    private class FieldInfoCache {
        Class<?> checkClazz;
        Field field;
        boolean needNest = false;

        boolean needBeCollected = false;

        public FieldInfoCache() {
        }

        public FieldInfoCache(Field field) {
            this.checkClazz = field.getType();
            if (Collection.class.isAssignableFrom(this.checkClazz)) {
                ResolvableType resolvableType = ResolvableType.forField(field).as(Collection.class);
                this.checkClazz = resolvableType.getGenerics()[0].resolve();
            } else if (Map.class.isAssignableFrom(this.checkClazz)) {
                ResolvableType resolvableType = ResolvableType.forField(field).as(Map.class);
                this.checkClazz = resolvableType.getGenerics()[1].resolve();
            }
            if (this.checkClazz == null) {
                this.needNest = false;
            } else {
                this.needNest = fieldNeedNestedMatcher.match(this.checkClazz.getPackage().getName());
            }
            this.needBeCollected = needBeCollected(field);
            this.field = field;
        }
    }

    private Map<String, FieldInfoCache> fieldInfoCacheMap(Class<?> instanceClazz) {

        return fieldCache.computeIfAbsent(instanceClazz, i -> buildFieldInfoCacheMap(instanceClazz));

    }

    private FieldInfoCache fieldInfoCache(String field, Class<?> instanceClazz) {
        Map<String, FieldInfoCache> map = fieldInfoCacheMap(instanceClazz);
        FieldInfoCache fieldInfoCache = map.get(field);
        if (fieldInfoCache != null) {
            return fieldInfoCache;
        }
        return NULL;
    }

    private Map<String, FieldInfoCache> buildFieldInfoCacheMap(Class<?> instanceClazz) {

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(instanceClazz);
        HashMap<String, FieldInfoCache> result = Maps.newHashMapWithExpectedSize(propertyDescriptors.length);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            Field declaredField;
            try {
                declaredField = instanceClazz.getDeclaredField(fieldName);
                result.put(fieldName, new FieldInfoCache(declaredField));
            } catch (NoSuchFieldException ignored) {
            }

        }
        return result;
    }


}
