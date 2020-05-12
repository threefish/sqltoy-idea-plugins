package com.gitee.threefish.sqltoy.linemarker.function;

import com.intellij.util.Function;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class FunctionTooltip implements Function {

    String msg = "点我快速切换至对应文件";

    public FunctionTooltip() {
    }

    public FunctionTooltip(String msg) {
        this.msg = msg;
    }


    @Override
    public Object fun(Object o) {
        return msg;
    }
}
