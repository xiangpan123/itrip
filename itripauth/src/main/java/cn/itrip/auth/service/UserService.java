package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;

public interface UserService {
    ItripUser selectUserCode(String userCode) throws Exception;
    ItripUser login(String userCode,String password) throws  Exception;
    void registeByPhone(ItripUser user) throws Exception;
    boolean validatPhone(String user ,String code) throws Exception;

}
