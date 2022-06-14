package com.zyh.community.controller;

import com.google.code.kaptcha.Producer;
import com.sun.mail.imap.protocol.ID;
import com.zyh.community.entity.User;
import com.zyh.community.service.UserService;
import com.zyh.community.until.CommunityConstant;
import com.zyh.community.until.CommunityUnity;
import com.zyh.community.until.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @Description
 * @create 2022-05-20 10:54
 */
@Controller
public class LoginController implements CommunityConstant{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kapthchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model,User user){
        Map<String,Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，已经向您的邮箱发送激活码");
            model.addAttribute("target","/community/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int result =  userService.activation(userId,code);
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，已经可以登录");
            model.addAttribute("target","/community/login");
        }else if (result == ACTIVATION_FAILURE){
            model.addAttribute("msg","无效操作，该账户已经激活");
            model.addAttribute("target","/community/index");
        }else{
            model.addAttribute("msg","激活失败，激活码不正确");
            model.addAttribute("target","/community/login");
        }
        return "/site/operate-result";
    }

    //获取验证码
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response){
        //生成验证码
        String text = kapthchaProducer.createText();
        BufferedImage image = kapthchaProducer.createImage(text);

        //验证码归属，生成一个随机字符串，用于表示字符串的拥有者
        String kaptchaOwner = CommunityUnity.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存入redis，用于之后验证
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    //登录
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public  String login(String username,String password,String code,boolean remeberMe,
                         Model model,HttpServletResponse response,
                         @CookieValue("kaptchaOwner")String kaptchaOwner){
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)){
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }
        //检查验证码
        if (StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)||!code.equals(kaptcha)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        //检查账号密码
        int expired = remeberMe?CommunityConstant.REMEMBER_EXPIRED_SECONDS:CommunityConstant.DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expired);

        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expired);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
