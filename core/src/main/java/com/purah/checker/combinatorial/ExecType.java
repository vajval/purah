package com.purah.checker.combinatorial;

public class ExecType {
    public enum Main {
        all_success(0),
        all_success_but_must_check_all(1),
        at_least_one(2),
        at_least_one_but_must_check_all(3);
        final int value;


        Main(int value) {
            this.value = value;
        }
        public int value(){
            return value;
        }
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
        checker_instance(1),
        instance_checker(2);
        final int value;


        Matcher(int value) {
            this.value = value;
        }

        public int value(){
            return value;
        }
    }
}
