package org.purah.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class People {

    String id;
    @TestAnn("不超过3个字")
    String name;
    @TestAnn("南方城市")
    String address;
    List<People> child;

    private static Map<String, People> testPeople = testPeople();
    public static final String str = "{" +
            "  \"name\": \"Elder\"," +
            "  \"address\": \"Tongliao\"," +
            "  \"age\": 70," +
            "  \"child\": [" +
            "    {" +
            "      \"name\": \"son\"," +
            "      \"address\": \"Beijing\"," +
            "      \"age\": 40," +
            "      \"child\": [" +
            "        {" +
            "          \"name\": \"grandson\"," +
            "          \"address\": \"Beijing\"," +
            "          \"age\": 10" +
            "        }," +
            "        {" +
            "          \"name\": \"granddaughter\"," +
            "          \"address\": \"Beijing\"," +
            "          \"age\": 10" +
            "        }" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"name\": \"daughter\"," +
            "      \"address\": \"Shanghai\"," +
            "      \"age\": 40," +
            "      \"child\": [" +
            "        {" +
            "          \"name\": \"grandson for daughter\"," +
            "          \"address\": \"Shanghai\"," +
            "          \"age\": 10" +
            "        }," +
            "        {" +
            "          \"name\": \"granddaughter for daughter\"," +
            "          \"address\": \"Shanghai\"," +
            "          \"age\": 10" +
            "        }" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"name\": \"unknown\"," +
            "      \"address\": null," +
            "      \"age\": null" +
            "    }" +
            "  ]" +
            "}";

    public People(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }


    public static People elder;
    public static People son;
    public static People daughter;
    public static People grandson;
    public static People granddaughter;
    public static People grandsonForDaughter;
    public static People granddaughterForDaughter;

    public static People unknown;

    public People() {
        this.id = UUID.randomUUID().toString().substring(0, 3);
    }

    public static People of(String name) {
        return testPeople.get(name);
    }


    public static Map<String, Object> mapOf(String name) {
        Gson gson = new Gson();
        Map<String, Object> people = gson.fromJson(str, Map.class);
        return people;
    }




    public static Map<String, People> testPeople() {

        Gson gson = new Gson();
        People people = gson.fromJson(str, People.class);

        Map<String, People> map = new HashMap<>();
        elder = people;
        son = people.child.get(0);
        daughter = people.child.get(1);
        grandson = son.child.get(0);
        granddaughter = son.child.get(1);
        grandsonForDaughter = daughter.getChild().get(0);
        granddaughterForDaughter =  daughter.getChild().get(1);

        unknown = people.child.get(2);

        return map;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
                ", address='" + address + '\'' +
                ", child=" + child +
                '}';
    }
}