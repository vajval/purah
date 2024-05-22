package org.purah.core.base;

import java.lang.annotation.Annotation;
import java.util.List;

public class PurahEnableMethodConfig {

    List<Class<? extends Annotation>> annClassList;

    List<Class<?>> argList;

    List<Class<?>> allowReturnType;
    int checkIndex = 0;




    public PurahEnableMethodConfig(List<Class<? extends Annotation>> annClassList, List<Class<?>> argList, int checkIndex, List<Class<?>> allowReturnType) {
        this.argList = argList;
        this.annClassList = annClassList;
        this.checkIndex = checkIndex;
        this.allowReturnType = allowReturnType;
    }
}
