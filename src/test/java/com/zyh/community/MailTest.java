package com.zyh.community;

import com.zyh.community.until.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;

import javax.crypto.Cipher;
import javax.naming.Context;


@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient client;
    @Autowired
    private TemplateEngine engine;
    @Test
    public void test(){
        client.sendMail("18980618970@163.com","TEST","HelloWord");
    }
    @Test
    public void testHtml(){
        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariable("username","Caleb");
        String content =  engine.process("/mail/demo.html",context);
        client.sendMail("18980618970@163.com","HTML",content);
    }
}
