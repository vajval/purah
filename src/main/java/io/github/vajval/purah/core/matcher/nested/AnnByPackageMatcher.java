package io.github.vajval.purah.core.matcher.nested;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.singlelevel.WildCardMatcher;
import org.apache.commons.beanutils.PropertyUtils;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.inft.ListIndexMatcher;
import io.github.vajval.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
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


    protected FieldInfo NULL = new FieldInfo();

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
        Map<String, FieldInfo> fieldInfoCacheMap = fieldInfoCacheMap(belongInstance.getClass());
        Set<String> result = Sets.newHashSet();
        for (String field : fields) {
            FieldInfo fieldInfo = fieldInfoCacheMap.get(field);
            if (fieldInfo.needBeCollected || fieldInfo.needNest) {
                result.add(field);
            }
        }
        return result;
    }


    @Override
    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        FieldInfo fieldInfo = fieldInfoCache(matchedField, inputArg.argClass());

        boolean needNest = fieldInfo.needNest;
        boolean needBeCollected = fieldInfo.needBeCollected;

        if (fieldInfo == NULL) {
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
                return NestedMatchInfo.justCollected;
            }
        } else {
            if (needNest) {
                return NestedMatchInfo.justNested(childFieldMatcher());
            } else {
                return NestedMatchInfo.ignore;
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

        return true;
    }

    private class FieldInfo {
        Class<?> checkClazz;
        Field field;
        boolean needNest = false;

        boolean needBeCollected = false;

        public FieldInfo() {
        }

        public FieldInfo(Field field) {
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

    private Map<String, FieldInfo> fieldInfoCacheMap(Class<?> instanceClazz) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(instanceClazz);
        HashMap<String, FieldInfo> result = Maps.newHashMapWithExpectedSize(propertyDescriptors.length);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            Field declaredField;
            try {
                declaredField = instanceClazz.getDeclaredField(fieldName);
                result.put(fieldName, new FieldInfo(declaredField));
            } catch (NoSuchFieldException ignored) {
            }

        }
        return result;

    }

    private FieldInfo fieldInfoCache(String field, Class<?> instanceClazz) {
        Map<String, FieldInfo> map = fieldInfoCacheMap(instanceClazz);
        FieldInfo fieldInfo = map.get(field);
        if (fieldInfo != null) {
            return fieldInfo;
        }
        return NULL;
    }


}
