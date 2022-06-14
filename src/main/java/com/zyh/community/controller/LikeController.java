package com.zyh.community.controller;

import com.zyh.community.entity.User;
import com.zyh.community.service.LikeService;
import com.zyh.community.until.CommunityUnity;
import com.zyh.community.until.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @Description
 * @create 2022-06-09 10:00
 */
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId){
        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityUserId,entityType,entityId);
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        //点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        return CommunityUnity.getJSONString(0,null,map);
    }
}
