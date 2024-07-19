package org.purah.util;

public final class TestUser {
    private final Long id;
    @TestAnn("name")
    public final String name;
    public final String address;

    public TestUser child;

    public People people;

    public TestUser(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public TestUser getChild() {
        return child;
    }

    public People getPeople() {
        return people;
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", child=" + child +
                ", people=" + people +
                '}';
    }
}