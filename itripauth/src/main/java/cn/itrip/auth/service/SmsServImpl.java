package cn.itrip.auth.service;

import com.cloopen.rest.sdk.CCPRestSmsSDK;

import java.util.HashMap;

public class SmsServImpl implements SmsService{
    @Override
    public void sendSms(String to, String templateId, String[] datas) throws Exception {
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init("app.cloopen.com", "8883");
        // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
        restAPI.setAccount("8aaf070870e20ea101713868614d2dee", "83f7d887c9ac4f1c9f6abcbc52968f0a");
        // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在控制首页中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
        restAPI.setAppId("8aaf070870e20ea10171386861a22df4");
        // 请使用管理控制台中已创建应用的APPID。
        result = restAPI.sendTemplateSMS(to,templateId,datas);
        System.out.println("SDKTestGetSubAccounts result=" + result);
        if("000000".equals(result.get("statusCode"))){
            System.out.println("短信发送成功");
        }else{
            throw new Exception("短信发送失败");
        }
    }
}
