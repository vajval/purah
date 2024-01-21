package com.purah.checker.combinatorial;

public class ExecType {
    public enum Main {
        all_success,
        all_success_but_must_check_all,
        at_least_one,
        at_least_one_but_must_check_all;

        public static Main valueOf(int value) {

            if (value == 0) {
                return all_success;
            } else if (value == 2) {
                return all_success_but_must_check_all;
            } else if (value == 3) {
                return at_least_one;
            } else if (value == 4) {
                return at_least_one_but_must_check_all;
            } else {
                throw new RuntimeException();
            }
        }
    }

    public enum Matcher {
        rule_instance,
        instance_instance

    }
}
