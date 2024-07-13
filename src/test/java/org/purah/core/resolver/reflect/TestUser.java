package org.purah.core.resolver.reflect;

import org.purah.util.People;
import org.purah.util.TestAnn;

public final class TestUser {
    private Long id;
    @TestAnn("name")
    public String name;
    public String address;

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
}