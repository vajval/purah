package org.purah.core;

import org.purah.core.matcher.ann.FieldType;

import java.util.Collections;
import java.util.Map;

public class TestObject {
    @FieldType("123")
    public String a;

    public String ab;

    public String abc;

    public TestObject child;


    protected Map<String, Map<String, String>> map;


    public static TestObject create() {
        TestObject testObject = new TestObject();
        testObject.a = "a";
        testObject.ab = "ab";
        testObject.abc = "abc";
        testObject.map = Collections.singletonMap("mapKey", Collections.singletonMap("key", "value"));
        return testObject;
    }

    public TestObject getChild() {
        return child;
    }

    public void setChild(TestObject child) {
        this.child = child;
    }

    public Map<String, Map<String, String>> getMap() {
        return map;
    }

    public void setMap(Map<String, Map<String, String>> map) {
        this.map = map;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getAb() {
        return ab;
    }

    public void setAb(String ab) {
        this.ab = ab;
    }

    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }
}