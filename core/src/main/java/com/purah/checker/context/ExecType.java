package com.purah.checker.context;

public class ExecType {
  public    enum Main {
        all_success,
         all_success_but_must_check_all,
         at_least_one,
         at_least_one_but_must_check_all
    }

    public    enum Matcher {
         checker_instance,
        instance_checker


    }
}
