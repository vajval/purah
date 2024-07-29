package io.github.vajval.purah.core.matcher.inft;

import java.util.List;
import java.util.Map;


/**
 *
 * 从list中获取匹配的数据,不实现也能用,就是对每个下标生成字符串,在挨个匹配效率地
 * Retrieve data from a list.
 * If this interface is not implemented, all indices in the list will be encapsulated as #1, #2, #3 or [0], [1], [2].
 * This approach will result in very low efficiency.
 */


public interface ListIndexMatcher {

    Map<String, Object> listMatch(List<?> objectList);

}
