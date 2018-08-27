package com.mantic.control.api.account;

import com.mantic.control.api.Url;

/**
 * Created by jayson on 2017/6/20.
 */

public interface AccountUrl {
    String BASE_URL = Url.ACCOUNT_URL+"api/v2/";
    // 注册
    String REGISTER = "mobile/register";
    //找回密码
    String RETRIEVE = "mobile/password/change";
    //修改密码
    String MODIFY = "user/password/change";
    //登录
    String LOGIN = "login/app";
    //登出
    String LOGOUT = "logout";
    //刷新token
    String REFRESHTOKEN = "refreshtoken";
    //用户信息
    String USERINFO = "userinfo";
    //用户信息
    String CHECKTOKEN = "check/token";
    //更新用户信息
    String USERUPDATE = "userupdate";


    //配网状态查询接口
    String STATUSQUERY = "device/status/query";
    //开始配网接口
    String NETWORKSTART = "device/network/start";
    //配网轮询接口
    String NETWORKCHECK = "device/network/check";
    //绑定接口
    String DEVICEBIND = "device/bind";
    //解绑接口
    String DEVICEUNBIND = "device/unbind";

    //获取验证码
    String VERIFICATIONCODE = "mobile/verifycode/query";

    //获取配置接口
    String DEVICEGETINFO = "device/get/info";

    //获取配置接口
    String SETJDSWITCH = "device/jdskill/set/switch";


    //JD登录接口 根据code 拿 token
    String JDAUTHORIZE = "jingdong/authorize/success";

}
