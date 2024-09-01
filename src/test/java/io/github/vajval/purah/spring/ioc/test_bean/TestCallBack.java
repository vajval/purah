package io.github.vajval.purah.spring.ioc.test_bean;

import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.spring.ioc.PurahIocRegS;
import io.github.vajval.purah.spring.ioc.refresh.PurahRefreshCallBack;
import io.github.vajval.purah.core.Purahs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCallBack implements PurahRefreshCallBack {
    @Autowired
    PurahConfigPropertiesBean purahConfigPropertiesBean;

    @Override
    public void exec(Purahs purahs) {
        PurahContext purahContext = purahs.purahContext();
        PurahIocRegS purahIocRegS = new PurahIocRegS(purahContext);
        purahIocRegS.regCheckerByProperties(purahConfigPropertiesBean);
    }
}
