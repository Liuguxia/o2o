package com.imooc.o2o.util;

import com.google.code.kaptcha.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pc on 2019/2/6.
 */
public class CodeUtil {
    public static boolean checkVerifyCode(HttpServletRequest request){
        //期待的验证
        String verifyCodeExpected= (String) request.getSession().getAttribute(
                com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
        //输入的验证码                                                          verifyCodeActual
        String verifyCodeActual=HttpServletRequestUtil.getString(request,"verifyCodeActual");

        //比较两个验证码
        if (verifyCodeActual==null||!verifyCodeActual.equals(verifyCodeExpected)){
            return false;
        }
        //相等的话，就返回true
        return true;
    }
}
