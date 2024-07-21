package org.purah.core.checker.combinatorial;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.checker.ComboBuilderChecker;
import org.purah.core.checker.GenericsProxyChecker;
import org.purah.core.checker.LambdaChecker;
import org.purah.core.checker.factory.LambdaCheckerFactory;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.MultiCheckResult;
import org.purah.core.checker.result.ResultLevel;
import org.purah.core.matcher.nested.FixedMatcher;
import org.purah.core.matcher.nested.GeneralFieldMatcher;
import org.purah.core.matcher.singlelevel.AnnTypeFieldMatcher;
import org.purah.core.matcher.singlelevel.FieldType;

import java.util.Objects;

class CombinatorialCheckerTest {


    PurahContext purahContext;
    ComboBuilderChecker comboBuilderChecker;


    final User alice = new User(1L, "alice");
    final User bob = new User(2L, "bob");
    final Trade trade = new Trade(alice, bob, 1.25, "abc");


    @BeforeEach
    public void beforeEach() {
        purahContext = new PurahContext();

        LambdaCheckerFactory<Number> idCheck = LambdaCheckerFactory.of(Number.class).build("id is *", (a, inputArg) -> {
            String id = a.replace("id is ", "");
            return inputArg.intValue() == Integer.parseInt(id);
        });

        LambdaCheckerFactory<String> nameCheck = LambdaCheckerFactory.of(String.class).build("name is *", (a, b) -> {
            String name = a.replace("name is ", "");
            return Objects.equals(name, b);
        });
        LambdaChecker<String> abcCheck = LambdaChecker.of(String.class).build("no abc", i -> !i.contains("abc"));
        purahContext.checkManager().reg(abcCheck);
        purahContext.checkManager().addCheckerFactory(idCheck);
        purahContext.checkManager().addCheckerFactory(nameCheck);
        comboBuilderChecker = purahContext.combo().match(new GeneralFieldMatcher("initiator.id"), "id is 1")        //√
                .match(new AnnTypeFieldMatcher("shortText"), "no abc")            //x
                .match(new GeneralFieldMatcher("initiator.name"), "name is alice")//√
                .resultLevel(ResultLevel.all);

        purahContext.combo().resultLevel(ResultLevel.all).match(new FixedMatcher("id"), "id is 1").match(new FixedMatcher("name"), "name is alice").reg("user_test");


    }

    @Test
    public void test() {
        CombinatorialCheckerConfig config = CombinatorialCheckerConfig.create(purahContext);
        config.addMatcherCheckerName(new FixedMatcher("initiator"), Lists.newArrayList("user_test"));
        config.addMatcherCheckerName(new FixedMatcher("recipients"), Lists.newArrayList("user_test"));
        config.setMainExecType(ExecMode.Main.all_success_but_must_check_all);
        config.setResultLevel(ResultLevel.all);
        CombinatorialChecker combinatorialChecker = new CombinatorialChecker(config);

        MultiCheckResult<CheckResult<?>> multiCheckResult = combinatorialChecker.check(trade);
        Assertions.assertEquals(2, multiCheckResult.data().size());


        CheckResult<?> checkResult = multiCheckResult.data().get(0);
        Assertions.assertTrue(checkResult instanceof MultiCheckResult);
        MultiCheckResult<?> childResult = (MultiCheckResult) checkResult;
        Assertions.assertEquals(2, childResult.data().size());
        Assertions.assertTrue(childResult.data().get(0));
        Assertions.assertTrue(childResult.data().get(1));


        checkResult = multiCheckResult.data().get(1);
        Assertions.assertTrue(checkResult instanceof MultiCheckResult);
        childResult = (MultiCheckResult) checkResult;
        Assertions.assertEquals(1, childResult.data().size());
        Assertions.assertFalse(childResult.data().get(0));

    }

    @Test
    public void all() {
        ComboBuilderChecker checker = comboBuilderChecker.mainMode(ExecMode.Main.all_success);
        MultiCheckResult<CheckResult<?>> result = checker.check(trade);//
        Assertions.assertFalse(result);
        Assertions.assertEquals(result.data().size(), 2);
    }

    @Test
    public void all_success_but_must_check_all() {
        ComboBuilderChecker checker = comboBuilderChecker.mainMode(ExecMode.Main.all_success_but_must_check_all);
        MultiCheckResult<CheckResult<?>> result = checker.check(trade);//xx√
        Assertions.assertFalse(result);
        Assertions.assertEquals(result.data().size(), 3);
    }

    @Test
    public void at_least_one() {
        ComboBuilderChecker checker = comboBuilderChecker.mainMode(ExecMode.Main.at_least_one);
        MultiCheckResult<CheckResult<?>> result = checker.check(trade);//xx√
        Assertions.assertTrue(result);
        Assertions.assertEquals(result.data().size(), 1);
    }

    @Test
    public void at_least_one_but_must_check_all() {
        ComboBuilderChecker checker = comboBuilderChecker.mainMode(ExecMode.Main.at_least_one_but_must_check_all);
        MultiCheckResult<CheckResult<?>> result = checker.check(trade);//xx√
        Assertions.assertTrue(result);
        Assertions.assertEquals(result.data().size(), 3);
    }


    public static class Trade {
        @FieldType("shortText")
        String title;
        @FieldType("needCheck")

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


}



