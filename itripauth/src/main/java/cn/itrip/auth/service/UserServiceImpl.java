package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisUtil;
import cn.itrip.dao.user.ItripUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private ItripUserMapper itripUserMapper;
    @Resource
    private SmsService smsService;
    @Resource
    private RedisUtil redisUtil;
    @Override
    public ItripUser selectUserCode(String userCode) throws Exception {
        Map map = new HashMap();
        map.put("userCode",userCode);
        List<ItripUser> list = itripUserMapper.getItripUserListByMap(map);
        if(list != null && list.size() >0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public ItripUser login(String userCode, String password) throws Exception {
        ItripUser user = selectUserCode(userCode);
        if(user != null && user.getUserPassword().equals(password)){
            return user;
        }
        return null;
    }

    @Override
    public void registeByPhone(ItripUser user) throws Exception {
        //生成用户 插入一条数据
        itripUserMapper.insertItripUser(user);
        //生成激活码
        int randomCode = MD5.getRandomCode();
        //发短信
        smsService.sendSms(user.getUserCode(),"1",new String[]{randomCode+""});
        //保存到rides
        redisUtil.setString("activation"+user.getUserCode(),randomCode+"",2*60);
    }

    @Override
    public boolean validatPhone(String user, String code) throws Exception {
        String value = redisUtil.getString("activation" + user);
        if(value != null && value.equals(code)){
            ItripUser itripUser = selectUserCode(user);
            if(itripUser != null){
                itripUser.setActivated(1);
                itripUser.setUserType(0);
                itripUser.setFlatID(itripUser.getId());
                itripUserMapper.updateItripUser(itripUser);
                return true;
            }
        }
        return false;
    }
}
