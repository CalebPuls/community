package com.zyh.community.controller;

import com.zyh.community.entity.Comment;
import com.zyh.community.entity.DiscussPost;
import com.zyh.community.entity.Page;
import com.zyh.community.entity.User;
import com.zyh.community.service.CommentService;
import com.zyh.community.service.DiscussPostService;
import com.zyh.community.until.CommunityConstant;
import com.zyh.community.until.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * @author
 * @Description
 * @create 2022-06-04 11:46
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPostService;
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + discussPostId;
    }
    //用户的所有回复
    @RequestMapping(path = "/userReply",method = RequestMethod.GET)
    public String findUserReplys(Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/comment/userReply");
        int replyCount = commentService.findCommentCountByUserId(user.getId(),CommunityConstant.ENTITY_TYPE_POST);
        page.setRows(replyCount);
        model.addAttribute("replyCount",replyCount);
        List<Map<String,Object>> res = new ArrayList<>();
        List<Comment> comments = commentService.findCommentsByUserId(user.getId(),
                CommunityConstant.ENTITY_TYPE_POST,page.getOffset(),page.getLimit());
        if (comments != null){
            for (Comment c : comments){
                Map<String,Object> map = new HashMap<>();
                map.put("comment",c);
                map.put("post",discussPostService.findDiscussPost(c.getEntityId()));
                res.add(map);
            }
        }
        model.addAttribute("replys",res);
        return "/site/my-reply";
    }
}
