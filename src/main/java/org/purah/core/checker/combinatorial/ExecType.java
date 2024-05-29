package org.purah.core.checker.combinatorial;


public class ExecType {
    public enum Main {


        // 要求必须要全部成功，才算成功,发现有错误就不继续了，后面的被填充为ignore
        all_success(0),
        // 要求必须要全部成功，才算成功，但是必须检查完,发现有错误也要继续
        all_success_but_must_check_all(1),
        //只要一个成功就够了
        at_least_one(2),
        //只要一个成功就够了，但是必须检查完
        at_least_one_but_must_check_all(3);
        final int value;


        Main(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Main valueOf(int value) {

            if (value == 0) {
                return all_success;
            } else if (value == 1) {
                return all_success_but_must_check_all;
            } else if (value == 2) {
                return at_least_one;
            } else if (value == 3) {
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

        public int value() {
            return value;
        }
    }
}
