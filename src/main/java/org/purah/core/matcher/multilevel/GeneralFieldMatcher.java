package org.purah.core.matcher.multilevel;


import com.google.common.base.Splitter;
import org.purah.core.base.Name;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Name("general")
public class GeneralFieldMatcher extends AbstractMultilevelFieldMatcher {

    FieldMatcher firstLevelFieldMatcher;
    String firstLevelStr;
    String childStr;

    List<GeneralFieldMatcher> wrapList = null;


    public GeneralFieldMatcher(String matchStr) {
        super(matchStr);
        if (this.matchStr.contains("|")) {
            wrapList = Splitter.on("|").splitToList(matchStr).stream().map(GeneralFieldMatcher::new).collect(Collectors.toList());
            return;
        }

        int index = matchStr.indexOf(".");
        firstLevelStr = matchStr;
        childStr = "";
        if (index != -1) {
            firstLevelStr = matchStr.substring(0, index);
            childStr = matchStr.substring(index + 1);
        }
        index = firstLevelStr.indexOf("#");
        if (index != -1 && index != 0) {
            childStr = firstLevelStr.substring(index) + "." + childStr;
            firstLevelStr = matchStr.substring(0, index);
        }
        if (!StringUtils.hasText(childStr)) {
            childStr = null;
        }
        firstLevelFieldMatcher = new WildCardMatcher(firstLevelStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        if (firstLevelFieldMatcher != null) {
            return firstLevelFieldMatcher.match(field);
        }
        for (GeneralFieldMatcher generalFieldMatcher : wrapList) {
            if (generalFieldMatcher.match(field)) {
                return true;
            }
        }
        return false;

    }


    @Override
    public MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject) {
        if (wrapList != null) {
            List<FieldMatcher> fieldMatchers = new ArrayList<>();
            boolean addToFinal = false;
            if(matchedField.equals("child")){
                System.out.println(123);
            }
            for (GeneralFieldMatcher generalFieldMatcher : wrapList) {
                if (generalFieldMatcher.match(matchedField, instance)) {
                    MultilevelMatchInfo multilevelMatchInfo = generalFieldMatcher.childFieldMatcher(instance, matchedField, matchedObject);
                    addToFinal = addToFinal || multilevelMatchInfo.isAddToFinal();
                    if (multilevelMatchInfo.getChildFieldMatcherList() != null) {
                        fieldMatchers.addAll(multilevelMatchInfo.getChildFieldMatcherList());
                    }
                }
            }
            if (addToFinal) {
                return MultilevelMatchInfo.addToFinalAndChildMatcher(fieldMatchers);
            }
            return MultilevelMatchInfo.justChild(fieldMatchers);
        }
        if (childStr == null) {
            return MultilevelMatchInfo.addToFinal();
        }


        FieldMatcher fieldMatcher;
        if (childStr.contains(".") || childStr.contains("#")) {
            fieldMatcher = new GeneralFieldMatcher(childStr);
        } else {
            fieldMatcher = new WildCardMatcher(childStr);
        }
        return MultilevelMatchInfo.justChild(fieldMatcher);
    }


    @Override
    public String toString() {
        return "GeneralFieldMatcher{" +
                "firstLevelFieldMatcher=" + firstLevelFieldMatcher +
                ", firstLevelStr='" + firstLevelStr + '\'' +
                ", childStr='" + childStr + '\'' +
                '}';
    }
}
//
