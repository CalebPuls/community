package com.zyh.community.controller;

import com.zyh.community.service.AlphaService;
import com.zyh.community.until.CommunityUnity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author
 * @Description
 * @create 2022-05-11 13:41
 */

@Controller
@RequestMapping("/alpha")
public class Alpha {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/sayHello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("select")
    @ResponseBody
    public String select(){
        return alphaService.select();
    }

    @RequestMapping("http")
     public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求的数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> e = request.getHeaderNames();
        while(e.hasMoreElements()){
            String key =  e.nextElement();
            String value = request.getHeader(key);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("String");
        try {
            PrintWriter writer = response.getWriter();
            writer.write("nowcoder");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    // /student?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String students(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10") int limit
    ){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return  "a student";
    }

    //POST请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudebt(String name,int age){
        return "success save " + name + age;
    }

    //响应html数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("name","张三");
        mv.addObject("age","35");
        mv.setViewName("/demo/view");
        return mv;
    }

    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","电子科技大学");
        model.addAttribute("age",70);
        return "/demo/view";
    }

    //响应JASON数据（异步请求）
    //java对象 -> JASON字符串 -> js对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",25);
        emp.put("salary",16000);
        return emp;
    }
    //多个
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        Map<String,Object> emp1 = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",25);
        emp.put("salary",16000);
        list.add(emp);
        emp1.put("name","李四");
        emp1.put("age",25);
        emp1.put("salary",16000);
        list.add(emp1);
        return list;
    }

    @RequestMapping(path = "/reqJASON",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getHtml(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","Caleb");
        map.put("age",24);
        map.put("salary",20000);
        return map;
    }

    //cookie示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", CommunityUnity.generateUUID());
        //设置cookie的访问路径
        cookie.setPath("/community/alpha");
        //设置cookie存在的时间
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    //session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set session";
    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

}
