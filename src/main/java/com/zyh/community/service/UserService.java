package com.zyh.community.service;

import com.sun.javafx.collections.MappingChange;
import com.zyh.community.dao.LoginTicketMapper;
import com.zyh.community.dao.UserMapper;
import com.zyh.community.entity.LoginTicket;
import com.zyh.community.entity.User;
import com.zyh.community.until.CommunityConstant;
import com.zyh.community.until.CommunityUnity;
import com.zyh.community.until.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author
 * @Description
 * @create 2022-05-16 19:24
 */
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    //注册账户
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //判断是否为空
        if (user == null)throw new IllegalArgumentException("参数不能为空");
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证账号
        if(userMapper.selectByName(user.getUsername())!=null){
            map.put("usernameMsg","账号已存在");
            return map;
        }
        if(userMapper.selectByEmail(user.getEmail())!=null){
            map.put("emailMsg","邮箱已存在");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUnity.generateUUID().substring(0,5));
        user.setPassword(CommunityUnity.md5(user.getPassword())+user.getSalt());
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUnity.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/用户id/用户激活码
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"账户激活",content);
        return map;
    }

    //激活账户
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if (code.equals(user.getActivationCode())){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        } else{
            return ACTIVATION_FAILURE;
        }
    }

    //登录账户
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账户
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg","账户不存在");
            return map;
        }
        String temp = CommunityUnity.md5(password)+user.getSalt();
        if (!user.getPassword().equals(CommunityUnity.md5(password)+user.getSalt())){
            map.put("passwordMsg","密码错误");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg","该账户未激活");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUnity.generateUUID());
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    //退出账户
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    //通过ticket查询用户
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //更改headerurl
    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    //更改密码
    public int updatePassword(int userId,String password){
        return userMapper.updatePassword(userId,password);
    }
}
