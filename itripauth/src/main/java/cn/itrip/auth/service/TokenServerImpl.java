package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisUtil;
import cn.itrip.common.UserAgentUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenServerImpl implements TokenService {
    @Resource
    private RedisUtil redisUtil;
    @Override
    public String generateToken(String userAgent, ItripUser user) throws Exception {
        //token:PC-usercode(md5)-userid-creationDate-rendom(6)
        StringBuilder str =  new StringBuilder("token:");
        //判断是哪一种客户端
        if(UserAgentUtil.CheckAgent(userAgent)){
            str.append("MOBILE-");
        }else{
            str.append("PC-");
        }
        //拼接userCode （MD5加密后的）
        str.append(MD5.getMd5(user.getUserCode(),32)+"-");
        //拼接ID
        str.append(user.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.format(new Date()+"-");
        str.append(MD5.getMd5(userAgent,6));
        return str.toString();
    }


    @Override
    public void saveToken(String token ,ItripUser user) throws Exception {
        String josn = JSONObject.toJSONString(user);
        if(token.startsWith("token:PC-")){
            redisUtil.setString(token,josn,2*60*60);
        }else {
            redisUtil.setString(token,josn);
        }
    }
}
