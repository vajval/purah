package io.github.vajval.purah.core.checker.combinatorial;

import com.google.common.collect.Maps;
import io.github.vajval.purah.core.checker.result.ExecInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReOrder {
    private static final Logger logger = LogManager.getLogger(ReOrder.class);
    protected Map<Integer, Integer> fastStopIndexMap = new HashMap<>();
    protected Map<Integer, AtomicInteger> fastStopIndexCountMap = new HashMap<>();
    protected final AtomicInteger checkCount = new AtomicInteger(0);
    protected final int orderCount;
    protected final int size;
    protected final ExecInfo stopInfo;
    protected final String checkerName;

    public ReOrder(ExecMode.Main mainMode, int size, int orderCount, String name) {


        this.orderCount = orderCount;
        this.size = size;
        if (mainMode == ExecMode.Main.all_success) {
            stopInfo = ExecInfo.failed;
        } else {
            stopInfo = ExecInfo.success;
        }
        this.checkerName = name;
        for (int i = 0; i < size; i++) {
            fastStopIndexMap.put(i, i);
            fastStopIndexCountMap.put(i, new AtomicInteger(0));
        }

    }

    public List<CheckerExec> reOrder(List<CheckerExec> checkerExecs) {

        if (checkerExecs.size() != size) {
            logger.warn("checker " + checkerName + " 无法自动重排序,设定值长度与实际运行长度不同");
            return checkerExecs;
        }
        List<CheckerExec> result = new ArrayList<>(size);
        for (int i = 0; i < fastStopIndexMap.size(); i++) {
            Integer useIndex = fastStopIndexMap.get(i);
            result.add(checkerExecs.get(useIndex));
        }
        return result;

    }

    public void count(List<ExecInfo> execInfoList) {
        for (int index = 0; index < Math.min(size,execInfoList.size()); index++) {
            ExecInfo execInfo = execInfoList.get(index);
            if (execInfo == stopInfo) {
                fastStopIndexCountMap.get(index).addAndGet(1);
                break;
            }
        }
        int i = checkCount.addAndGet(1);
        if (i % orderCount == 0) {
            reMap();
            checkCount.set(0);
        }
    }

    protected void reMap() {
        //fastStopIndexMap:{0:2,1:0,2:1}  第一步执行第三个{0:2}, 第二步执行第一个{1:0}, 第三步执行第二个{2:1}
        List<Integer> sort = fastStopIndexCountMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().get(), a.getValue().get()))
                .map(Map.Entry::getKey).collect(Collectors.toList());

        //sort 2 0 1    当前第三步{2:1}执行的放最前面 (1), 当前第一步{0:2}执行的放第二(2) 当前第二步{1:0}执行的放第三(0)
        Map<Integer, Integer> newFastFailedStopMap = Maps.newHashMapWithExpectedSize(fastStopIndexMap.size());
        Map<Integer, AtomicInteger> newFastStopIndexCountMap = Maps.newHashMapWithExpectedSize(fastStopIndexMap.size());
        for (int index = 0; index < sort.size(); index++) {
            newFastFailedStopMap.put(index, fastStopIndexMap.get(sort.get(index)));
            newFastStopIndexCountMap.put(index, new AtomicInteger(0));
        }

        //fastStopIndexMap:{0:1,1:2,2:0}  第一步执行第2个, 第二执行第3个, 第三步执行第1个
        this.fastStopIndexMap = newFastFailedStopMap;
        this.fastStopIndexCountMap = newFastStopIndexCountMap;
    }

}


