package org.purah.core;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public  class People {

    String id;
    String name;
    List<People> child;

    public People(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<People> getChild() {
        return child;
    }

    public void setChild(List<People> child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "People{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", child=" + child +
                '}';
    }


    public static People of(String name) {
        return testPeople.get(name);
    }

    public static Map<String, People> testPeople = testPeople();

    public static Map<String, People> testPeople() {
        Map<String, People> map = Lists.newArrayList("长者", "儿子", "女儿", "孙子", "孙女", "外孙子", "外孙女").stream().map(People::new).collect(Collectors.toMap(People::getName, i -> i));
        map.get("长者").setChild(Lists.newArrayList(map.get("儿子"), map.get("女儿")));
        map.get("儿子").setChild(Lists.newArrayList(map.get("孙子"), map.get("孙女")));
        map.get("女儿").setChild(Lists.newArrayList(map.get("外孙子"), map.get("外孙女")));
        return map;

    }
}