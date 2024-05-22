package org.purah.core.matcher.clazz;




import org.purah.core.base.FieldGetMethodUtil;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.intf.FieldMatcherWithInstance;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractInstanceFieldMatcher extends BaseStringMatcher implements FieldMatcherWithInstance {

    protected ClassFieldsCaches classFieldsCaches;
    protected FieldGetMethodUtil fieldGetMethodUtil;

    public AbstractInstanceFieldMatcher(String matchStr) {
        super(matchStr);
        this.classFieldsCaches = new ClassFieldsCaches(this::getFieldsByClass);
        this.fieldGetMethodUtil = new FieldGetMethodUtil();
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        List<String> allFields = classFieldsCaches.getByInstanceClass(belongInstance.getClass());
        return fields.stream().filter(allFields::contains).collect(Collectors.toSet());
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        if (field == null || belongInstance == null) {
            return false;
        }
        return classFieldsCaches.getByInstanceClass(belongInstance.getClass()).contains(field);

    }

    public abstract List<String> getFieldsByClass(Class<?> clazz);
}
