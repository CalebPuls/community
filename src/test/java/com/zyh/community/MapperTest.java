package com.zyh.community;

import com.zyh.community.dao.DiscussPostMapper;
import com.zyh.community.dao.LoginTicketMapper;
import com.zyh.community.dao.UserMapper;
import com.zyh.community.entity.DiscussPost;
import com.zyh.community.entity.LoginTicket;
import com.zyh.community.entity.User;
import com.zyh.community.until.CommunityUnity;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @author
 * @Description
 * @create 2022-05-16 16:01
 */

@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class MapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void  testSelect(){
        System.out.println(userMapper.selectById(101));
        System.out.println(userMapper.selectByEmail("nowcoder11@sina.com"));
        System.out.println(userMapper.selectByName("aaa"));
    }
    @Test
    public void testInsertUpdate(){
        User user = new User();
        user.setUsername("Caleb");
        user.setPassword("123");
        user.setSalt("abc");
        user.setEmail("8@*.com");
        user.setHeaderUrl("htt[://www.");
        user.setCreateTime(new Date());
        System.out.println(userMapper.insertUser(user));

        userMapper.updateHeader(150,"http://www.zyh.com");
        userMapper.updatePassword(150,"888888");
        userMapper.updateStatus(150,6);
    }
    @Test
    public void testDiscuss(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for(DiscussPost d : list)
            System.out.println(d);
        System.out.println(discussPostMapper.selectDiscussPostRow(149));
    }
    @Test
    public void testLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setExpired(new Date());
        loginTicket.setStatus(0);
        loginTicket.setUserId(102);
        loginTicket.setTicket("abcd");
        loginTicketMapper.insertLoginTicket(loginTicket);
        LoginTicket loginTicket1 = loginTicketMapper.selectByTicket("abcd");
        System.out.println(loginTicket1);
        loginTicketMapper.updateStatus("abcd",1);
    }
    @Test
    public void test(){
        System.out.println(CommunityUnity.md5("123")+"0cbb2");
    }
}
