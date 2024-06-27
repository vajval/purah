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
  public static final    String str = "{" +
          "  \"name\": \"长者\"," +
          "  \"address\": \"通辽\"," +
          "  \"age\": 70," +
          "  \"child\": [" +
          "    {" +
          "      \"name\": \"儿子\"," +
          "      \"address\": \"北京\"," +
          "      \"age\": 40," +
          "      \"child\": [" +
          "        {" +
          "          \"name\": \"孙子\"," +
          "          \"address\": \"北京\"," +
          "          \"age\": 10" +
          "        }," +
          "        {" +
          "          \"name\": \"孙女\"," +
          "          \"address\": \"北京\"," +
          "          \"age\": 10" +
          "        }" +
          "      ]" +
          "    }," +
          "    {" +
          "      \"name\": \"女儿\"," +
          "      \"address\": \"上海\"," +
          "      \"age\": 40," +
          "      \"child\": [" +
          "        {" +
          "          \"name\": \"外孙子\"," +
          "          \"address\": \"上海\"," +
          "          \"age\": 10" +
          "        }," +
          "        {" +
          "          \"name\": \"外孙女\"," +
          "          \"address\": \"上海\"," +
          "          \"age\": 10" +
          "        }" +
          "      ]" +
          "    }," +
          "    {" +
          "      \"name\": \"不知道在哪的孩子\"," +
          "      \"address\": null," +
          "      \"age\": null" +
          "    }" +
          "  ]" +
          "}";

    public People(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }


    public People() {
        this.id = UUID.randomUUID().toString().substring(0, 3);
    }

    public static People of(String name) {
        return testPeople.get(name);
    }

    public static void main(String[] args) {

    }

    public static Map<String, Object> mapOf(String name) {


        Gson gson = new Gson();
        Map<String, Object> people = gson.fromJson(str, Map.class);


        return people;
    }

    private static Map<String, People> testPeople = testPeople();



    public static Map<String, People> testPeople() {

        Gson gson = new Gson();
        People people = gson.fromJson(str, People.class);

        Map<String, People> map = new HashMap<>();
        map.put(people.name, people);

        map.put(people.child.get(2).name,people.child.get(2));

        map.put(people.child.get(0).name,people.child.get(0));
        map.put(people.child.get(1).name,people.child.get(1));
        map.put(people.child.get(0).child.get(0).name,people.child.get(0).child.get(0));
        map.put(people.child.get(0).child.get(1).name,people.child.get(0).child.get(1));
        map.put(people.child.get(1).child.get(0).name,people.child.get(1).child.get(0));
        map.put(people.child.get(1).child.get(1).name,people.child.get(1).child.get(1));
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