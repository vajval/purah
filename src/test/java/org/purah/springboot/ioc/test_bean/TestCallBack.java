package org.purah.springboot.ioc.test_bean;

import org.purah.core.Purahs;
import org.purah.springboot.ioc.PurahRefreshCallBack;
import org.springframework.stereotype.Component;

@Component
public class TestCallBack implements PurahRefreshCallBack {
    public static  int value = 0;

    @Override
    public void exec(Purahs purahs) {
        value++;
    }
}
