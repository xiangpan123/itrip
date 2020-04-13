package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;

public interface TokenService {
    String generateToken(String userAgent, ItripUser user) throws Exception;
    void saveToken(String token ,ItripUser user) throws Exception;
}
