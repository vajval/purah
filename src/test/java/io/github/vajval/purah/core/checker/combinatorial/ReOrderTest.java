package io.github.vajval.purah.core.checker.combinatorial;

import com.google.common.collect.Lists;
import io.github.vajval.purah.core.checker.result.ExecInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static com.google.common.collect.Lists.newArrayList;
import static io.github.vajval.purah.core.checker.result.ExecInfo.failed;
import static io.github.vajval.purah.core.checker.result.ExecInfo.success;
import static java.util.concurrent.TimeUnit.*;

class ReOrderTest {
    @Test
    public void test() {
        ReOrder reOrder = new ReOrder(ExecMode.Main.all_success, 3, 100, "3");
        reOrder.count(newArrayList(failed));
        reOrder.count(newArrayList(success, success, failed));
        reOrder.count(newArrayList(success, success, failed));
        reOrder.reMap();
        Assertions.assertEquals(reOrder.fastStopIndexMap.get(0), 2);//马上执行第二个,因为第二个容易错
        Assertions.assertEquals(reOrder.fastStopIndexMap.get(1), 0);//执行第1个,第1个比较容易错
        Assertions.assertEquals(reOrder.fastStopIndexMap.get(2), 1);//不容易出错的放最后啊
    }

    @Test
    public void te2st() {

        ReOrder reOrder = new ReOrder(ExecMode.Main.at_least_one, 3, 100, "3");
        reOrder.count(newArrayList(success));
        reOrder.count(newArrayList(failed, failed, success));
        reOrder.count(newArrayList(failed, failed, success));
        reOrder.reMap();
        Assertions.assertEquals(reOrder.fastStopIndexMap.get(0), 2);//马上执行第二个,因为第二个容易对
        Assertions.assertEquals(reOrder.fastStopIndexMap.get(1), 0);//执行第1个,第1个比较容易对
        Assertions.assertEquals(reOrder.fastStopIndexMap.get(2), 1);//不容易对的放最后啊
    }


    @Test
    public void abc() throws Exception {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 25, 100L,
                SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
        ReOrder reOrder = new ReOrder(ExecMode.Main.all_success, 5, 771, "3");
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Future<?> future = executor.submit(() -> {
                        Random random = new Random();
                        for (int e = 0; e < 10000; e++) {
                            List<ExecInfo> execInfoList = Lists.newArrayList(success, success, success, success);
                            int randomNumber = random.nextInt(5); // 生成0到4之间的随机数
                            execInfoList.add(randomNumber, failed);
                            execInfoList = execInfoList.subList(0, randomNumber + 1);
                            reOrder.count(execInfoList);
                        }
                    }
            );

            futures.add(future);

        }
        for (Future<?> future : futures) {
            future.get();
        }
        System.out.println(reOrder.fastStopIndexMap);
        System.out.println(reOrder.fastStopIndexCountMap);
        reOrder.reMap();
        System.out.println(reOrder.fastStopIndexMap);


    }
}