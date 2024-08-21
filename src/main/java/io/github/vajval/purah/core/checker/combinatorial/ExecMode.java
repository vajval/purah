package io.github.vajval.purah.core.checker.combinatorial;


import io.github.vajval.purah.core.exception.UnexpectedException;

public class ExecMode {
    public enum Main {

        // 对于被ignore的check,不被视为成功也不被视为失败,被视为不存在,没有这个判断,不参与组合判断的结果
        // Checks that are ignored are neither considered successful nor failed; they are treated as not checked and do not participate in composite judgments.
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
}
