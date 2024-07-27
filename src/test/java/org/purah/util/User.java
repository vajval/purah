package org.purah.util;


import org.purah.core.checker.ann.CNPhoneNum;
import org.purah.core.checker.ann.NotEmptyTest;
import org.purah.core.checker.ann.NotNull;
import org.purah.core.checker.ann.Range;
import org.purah.springboot.aop.ann.CheckIt;

@CheckIt("所有字段自定义注解检测")
public final class User {

    public static final User BAD_USER = new User(50L, null, "123", null);
    public static final User GOOD_USER = new User(1L, "vajva", "15509931234", 15);
    public static final User GOOD_USER_BAD_CHILD = goodUserBadChild();
    public static final User GOOD_USER_GOOD_CHILD = goodUserGoodChild();

    public static User goodUserBadChild() {
        User user = new User(3L, "vajva", "15509931234", 15);
        user.childUser = new User(50L, null, "123", null);

        return user;

    }

    public static User goodUserGoodChild() {
        User user = new User(3L, "vajva", "15509931234", 15);
        user.childUser = new User(3L, "vajva", "15509931234", 15);

        return user;

    }

    @Range(min = 1, max = 10, errorMsg = "range wrong")
    public Long id;
    @NotEmptyTest(errorMsg = "this field cannot empty")
    public String name;
    @CNPhoneNum(errorMsg = "phone num wrong")
    public String phone;


    @NotNull(errorMsg = "norBull")
    public Integer age;

    User childUser;

    public User(Long id, String name, String phone, Integer age) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getChildUser() {
        return childUser;
    }

    public void setChildUser(User childUser) {
        this.childUser = childUser;
    }
}

  