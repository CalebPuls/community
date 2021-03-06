package com.zyh.community.controller;

//import com.sun.deploy.net.HttpResponse;
//import com.sun.org.apache.xpath.internal.WhitespaceStrippingElementMatcher;
import com.zyh.community.annotation.LoginRequired;
import com.zyh.community.entity.User;
import com.zyh.community.service.FollowService;
import com.zyh.community.service.LikeService;
import com.zyh.community.service.UserService;
import com.zyh.community.until.CommunityConstant;
import com.zyh.community.until.CommunityUnity;
import com.zyh.community.until.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author
 * @Description
 * @create 2022-05-31 21:07
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private  String uploadPath;
    @Value("${community.path.domain}")
    private  String domain;
    @Value("${server.servlet.context-path}")
    private  String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","?????????????????????");
            return "/site/setting";
        }
        //??????????????????
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix == null){
            model.addAttribute("error","????????????????????????");
            return "/site/setting";
        }
        //????????????????????????
        fileName = CommunityUnity.generateUUID() + suffix;
        //???????????????????????????
        java.io.File dest = new java.io.File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
            throw new RuntimeException("??????????????????????????????????????????",e);
        }

        //????????????????????????????????????web???????????????
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(value = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //????????????????????????
        fileName = uploadPath + "/" + fileName;
        //????????????
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        try(
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
        ) {

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
        }
    }
    @RequestMapping(path = "/updatePassword",method = RequestMethod.POST)
    public String update(Model model,String oldPassword,String newPassword,@CookieValue("ticket") String ticket){
        User user = hostHolder.getUser();
        if (StringUtils.isBlank(oldPassword)){
            model.addAttribute("passwordMsg","??????????????????");
            return "/site/setting";
        }
        if (!user.getPassword().equals(CommunityUnity.md5(oldPassword)+user.getSalt())){
            model.addAttribute("passwordMsg","????????????");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(),CommunityUnity.md5(newPassword)+user.getSalt());
        return "redirect:/logout";
    }

    //????????????
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("??????????????????");
        }
        //??????
        model.addAttribute("user",user);
        model.addAttribute("likeCount",likeService.findUserLikeCount(userId));

        //????????????
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //?????????????????????????????????????????????????????????
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }


}
