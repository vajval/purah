package com.purah.checker.combinatorial;

import static org.junit.jupiter.api.Assertions.*;

class CombinatorialCheckerTest {


    /**
     * easy-rule:
     *   rules:
     *     - name: 贷款申请
     *       mapping:
     *         product_city_rate_wild_card:
     *           "[{直辖市}_rate}]": 直辖市利率标准检测
     *           "[{一线城市}_rate}]": 一线城市市利率标准检测
     *           "[{北方城市}_rate}]": 北方城市市利率标准检测
     *
     *         wild_card:
     *           "[num_*]" : 取值范围检测
     *         type_by_ann:
     *           "[短文本]" : 敏感词检查
     *           "[长文本]" : 敏感词检查
     */

}