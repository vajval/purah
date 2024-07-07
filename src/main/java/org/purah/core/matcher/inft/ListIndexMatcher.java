package org.purah.core.matcher.inft;

import java.util.List;
import java.util.Map;


/**
 * Retrieve data from a list.
 * If this interface is not implemented, all indices in the list will be encapsulated as #1, #2, #3 or [0], [1], [2].
 * This approach will result in very low efficiency.
 */


public interface ListIndexMatcher {

    Map<String, Object> listMatch(List<?> objectList);

}
