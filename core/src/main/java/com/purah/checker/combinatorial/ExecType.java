package com.purah.checker.combinatorial;

public class ExecType {
     enum Main {
        all_success,
         all_success_but_must_check_all,
         at_least_one,
         at_least_one_but_must_check_all
    }

     enum Matcher {
         rule_instance,
         instance_instance

    }
}
