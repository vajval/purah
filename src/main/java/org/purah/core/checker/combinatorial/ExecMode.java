package org.purah.core.checker.combinatorial;


import org.purah.core.exception.UnexpectedException;

public class ExecMode {
    public enum Main {

        // 全成功才行,有错不继续
        // Require all checkers to pass for success; if a  failed is found, stop further checks, Fill with "ignore" for fields that are not checked.
        all_success(0),
        // 全成功才行,有错也要检查完
        // Require all checkers to pass for success and continue checking even if failed are found.
        all_success_but_must_check_all(1),
        // 一个就行,有错不继续
        // Only one success is sufficient.
        at_least_one(2),
        // 一个就行,有错也要检查完
        // Only one success is sufficient, but all checks must be completed.
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
                throw new UnexpectedException("Error value for : ExecType.Main " + value);
            }
        }
    }


    /**
     * 用一个 checker 检查所有 field value,然后下一个 checker
     * 一个field value被所有checker检查 ,然后下一个 field value
     * Fine, here are two ways to check things:
     * Go through each arg and apply every single check, then move on to the next arg.
     * Apply one check to all the args, then move on to the next check.
     */
    public enum Matcher {
        checker_arg(1),
        arg_checker(2);
        final int value;


        Matcher(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }
}
