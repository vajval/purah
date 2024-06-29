package org.purah.core.matcher;

import java.lang.annotation.*;
import java.util.List;
import java.util.Map;


public interface ListIndexMatcher {


    Map<String, Object> listMatch(List<?> objectList);

}
