package cn.itrip.auth.controller;

import cn.itrip.auth.service.TokenService;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.MD5;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/api")
public class UserController {
    @Resource
    private TokenService tokenService;
    @Resource
    private UserService userService;
    @RequestMapping(value = "/dologin",method = RequestMethod.POST)
    @ResponseBody
    public Object dolongin(String name, String password, HttpServletRequest request) {
        try {
            //mysal验证密码
            ItripUser user = userService.login(name, MD5.getMd5(password,32));
            if(user==null){
                return DtoUtil.returnFail("用户名密码错误", ErrorCode.AUTH_PARAMETER_ERROR);
            }
            //生成token保存
            String userAgent = request.getHeader("User-Agent");
            String token = tokenService.generateToken(userAgent, user);
            tokenService.saveToken(token,user);
            //返回前端
            ItripTokenVO vo = new ItripTokenVO(token,System.currentTimeMillis()+2*60*60*1000,
                    System.currentTimeMillis());
            return DtoUtil.returnSuccess("登录成功",vo);

        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("登录失败",ErrorCode.AUTH_UNKNOWN);
        }

    }
    @RequestMapping(value = "/registebyohone",method = RequestMethod.POST)
    @ResponseBody
    public Dto registeByPhone(@RequestBody ItripUserVO vo){
        if(!validatePhone(vo.getUserCode())){
            return DtoUtil.returnSuccess("请输入正确的的手机号",ErrorCode.AUTH_ILLEGAL_USERCODE);

        }
        ItripUser user = new ItripUser();
        user.setUserCode(vo.getUserCode());
        user.setUserName(vo.getUserName());
        try {
            user.setUserPassword(MD5.getMd5(vo.getUserPassword(),32));
            userService.registeByPhone(user);
            return DtoUtil.returnSuccess("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("注册失败",ErrorCode.AUTH_UNKNOWN);
        }
    }
    @RequestMapping(value = "/validatphone",method = RequestMethod.PUT)
    @ResponseBody
    public Dto validatphone(String name,String code){
        try {
            if(userService.validatPhone(name,code)){
                return DtoUtil.returnSuccess("激活成功");
            }else{
                return DtoUtil.returnFail("激活失败",ErrorCode.AUTH_ACTIVATE_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_UNKNOWN);
        }
    }
    private  boolean validatePhone(String phoneNum){
        String  reg = "^1[35678]\\d{9}$";
        return Pattern.compile(reg).matcher(phoneNum).find();
    }
}
