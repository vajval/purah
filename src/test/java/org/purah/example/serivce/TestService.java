package org.purah.example.serivce;


import org.purah.core.checker.result.CheckResult;
import org.purah.example.customAnn.pojo.CustomUser;
import org.purah.springboot.ann.CheckIt;
import org.purah.springboot.ann.FillToMethodResult;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    //不符合规则时会抛出异常
    public void voidCheck(@CheckIt("非空判断FromTestBean") CustomUser customUser) {

    }
    // 将结果填充到返回信息里
    @FillToMethodResult
    public boolean checkBoolTest(@CheckIt("非空判断FromTestBean") CustomUser customUser) {
        return false;
    }

    // 如果你自定义了返回内容的话，会将自定义的内容一并返回
    @FillToMethodResult
    public CheckResult checkResult(@CheckIt("非空判断FromTestBean") CustomUser customUser) {
        return null;
    }
    // 支持根据通配符匹配
    @FillToMethodResult
    public boolean checkResult(@CheckIt("1取值必须在[*-*]之间判断FromTestBean") int num) {
        return false;
    }


}
