package com.zyh.community.controller;

import com.zyh.community.entity.DiscussPost;
import com.zyh.community.entity.Page;
import com.zyh.community.entity.User;
import com.zyh.community.service.DiscussPostService;
import com.zyh.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * @author
 * @Description
 * @create 2022-05-16 19:38
 */
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMvc会自动实列化model和page，并将page注入到model中
        //使用thymeleaf中可以直接调用page中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> res = new LinkedList<>();
        for(DiscussPost d:list){
            User user = userService.findUserById(d.getUserId());
            Map<String,Object> map = new HashMap<>();
            map.put("post",d);
            map.put("user",user);
            res.add(map);
        }

        model.addAttribute("discussPosts",res);
        return "/index.html";
    }
}
