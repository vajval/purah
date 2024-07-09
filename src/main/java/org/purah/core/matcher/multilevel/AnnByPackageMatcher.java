package org.purah.core.matcher.multilevel;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.singlelevel.WildCardMatcher;
import org.purah.core.matcher.inft.ListIndexMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.springframework.core.ResolvableType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The input parameter represents the type that requires nested checks.
 * If the class of the field is within the specified package path,
 * then the value of this field will undergo the same matching process.
 *
 * <p>
 * package my.company.project
 * <p>
 * People{
 *
 * @TestAnn People child;
 * @TestAnn String name;
 * String id;
 * }
 * <p>
 * new People(name:1,id:11 ,new People(2,22,null))
 * new AnnByPackageMatcher("my.company.*")  ---------      fieldCheck(field)->field.hasAnn(@TestAnn.class)
 * <p>
 * return {name:11,child.name:22,child.child:null}
 */


public abstract class AnnByPackageMatcher extends BaseStringMatcher implements MultilevelFieldMatcher, ListIndexMatcher {

    protected WildCardMatcher wildCardMatcher;

    protected Map<Class<?>, Map<String, FieldInfoCache>> fieldCache = new ConcurrentHashMap<>();

    public FieldInfoCache NULL = new FieldInfoCache();

    public AnnByPackageMatcher(String allowPackagePatch) {
        super(allowPackagePatch);
        this.wildCardMatcher = new WildCardMatcher(allowPackagePatch);
    }


    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Map<String, FieldInfoCache> fieldInfoCacheMap = fieldInfoCacheMap(belongInstance.getClass());
        Set<String> result = Sets.newHashSet();
        for (String field : fields) {
            FieldInfoCache fieldInfoCache = fieldInfoCacheMap.get(field);
            if (fieldInfoCache.needBeChecked) {
                result.add(field);
            }
        }
        return result;
    }


    /**
     * 搜集有注解的字段做返回值
     * 还有 没有注解但是需要解析的向下解析
     */






    @Override
    public MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        FieldInfoCache fieldInfoCache = fieldInfoCache(matchedField, inputArg.argClass());
        boolean needNest = fieldInfoCache.needNest;
        boolean needBeChecked = fieldInfoCache.needBeChecked;

        if (fieldInfoCache == NULL) {
            needNest = wildCardMatcher.match(childArg.argClass().getPackage().getName());
            needBeChecked = false;
        }


        if (needBeChecked) {
            if (needNest) {
                return MultilevelMatchInfo.addToFinalAndChildMatcher(this, childArg);
            } else {
                return MultilevelMatchInfo.addToFinal(childArg);
            }
        } else {
            if (needNest) {
                return MultilevelMatchInfo.justChild(this);
            } else {
                return MultilevelMatchInfo.ignore();
            }
        }

    }

    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(objectList.size());
        for (int index = 0; index < objectList.size(); index++) {
            Object o = objectList.get(index);
            if (o != null && wildCardMatcher.match(o.getClass().getPackage().getName())) {
                result.put("#" + index, o);
            }
        }
        return result;

    }


    protected abstract boolean needBeChecked(Field field);



    class FieldInfoCache {
        Class<?> checkClazz;
        Field field;
        boolean match = false;

        boolean needNest = false;

        boolean needBeChecked = false;

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
                this.needNest = wildCardMatcher.match(this.checkClazz.getPackage().getName());
            }
            this.needBeChecked = needBeChecked(field);
            this.match = this.needNest || this.needBeChecked;
            this.field = field;
        }
    }

    protected Map<String, FieldInfoCache> fieldInfoCacheMap(Class<?> instanceClazz) {

        return fieldCache.computeIfAbsent(instanceClazz, i -> buildFieldInfoCacheMap(instanceClazz));

    }

    protected FieldInfoCache fieldInfoCache(String field, Class<?> instanceClazz) {
        Map<String, FieldInfoCache> map = fieldInfoCacheMap(instanceClazz);
        FieldInfoCache fieldInfoCache = map.get(field);
        if (fieldInfoCache != null) {
            return fieldInfoCache;
        }
        return NULL;
    }

    protected Map<String, FieldInfoCache> buildFieldInfoCacheMap(Class<?> instanceClazz) {


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
