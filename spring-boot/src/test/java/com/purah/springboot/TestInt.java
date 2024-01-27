package com.purah.springboot;

import com.purah.springboot.ann.CheckIt;
import com.purah.springboot.ann.EnableOnPurahContext;

@EnableOnPurahContext
public interface TestInt {
    boolean test(@CheckIt("1213")Integer value);
}
