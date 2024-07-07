package org.purah.core;


import org.purah.core.matcher.extra.clazz.ann.FieldType;
import org.purah.core.matcher.extra.clazz.AnnTypeFieldMatcher;
import org.purah.core.matcher.extra.clazz.ClassNameMatcher;
import org.purah.core.matcher.WildCardMatcher;

public class Util {


    public static PurahContext defaultPurahContext() {

        PurahContext purahContext = new PurahContext();
        purahContext.matcherManager().regBaseStrMatcher(AnnTypeFieldMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(ClassNameMatcher.class);
        purahContext.matcherManager().regBaseStrMatcher(WildCardMatcher.class);


        return purahContext;
    }


    public static class Trade {
        @FieldType("短文本")
        String title;
        @FieldType("需要检测")

        User initiator;
        User recipients;

        double money;

        public Trade(User initiator, User recipients, double money, String title) {
            this.initiator = initiator;
            this.recipients = recipients;
            this.money = money;
            this.title = title;
        }

        public User getInitiator() {
            return initiator;
        }

        public void setInitiator(User initiator) {
            this.initiator = initiator;
        }

        public User getRecipients() {
            return recipients;
        }

        public void setRecipients(User recipients) {
            this.recipients = recipients;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class User {

        Long id;
        String name;

        public User(Long id, String name) {
            this.id = id;
            this.name = name;
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
    }

    public static User initiator = new User(1L, "张三");
    public static User recipients = new User(2L, "李四");
    public static double money = 1.25;
    public static Trade trade = new Trade(initiator, recipients, money,"sb 啊");
}
