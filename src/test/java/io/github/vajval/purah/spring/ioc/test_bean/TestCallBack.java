package io.github.vajval.purah.spring.ioc.test_bean;

import io.github.vajval.purah.spring.ioc.PurahRefreshCallBack;
import io.github.vajval.purah.core.Purahs;
import org.springframework.stereotype.Component;

@Component
public class TestCallBack implements PurahRefreshCallBack {
    public static  int value = 0;

    @Override
    public void exec(Purahs purahs) {
        value++;
    }
}
