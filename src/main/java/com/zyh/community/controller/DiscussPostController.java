package com.zyh.community.controller;

import com.zyh.community.dao.CommentMapper;
import com.zyh.community.entity.Comment;
import com.zyh.community.entity.DiscussPost;
import com.zyh.community.entity.Page;
import com.zyh.community.entity.User;
import com.zyh.community.service.CommentService;
import com.zyh.community.service.DiscussPostService;
import com.zyh.community.service.LikeService;
import com.zyh.community.service.UserService;
import com.zyh.community.until.CommunityConstant;
import com.zyh.community.until.CommunityUnity;
import com.zyh.community.until.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author
 * @Description
 * @create 2022-06-03 17:58
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant{
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    /**
     * 发布帖子
     * @param title 帖子标题
     * @param content 帖子内容
     * @return
     */
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUnity.getJSONString(403,"未登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDisscussPost(post);

        //报错情况之后统一处理
        return CommunityUnity.getJSONString(0,"发布成功!");
    }
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId, Model model, Page page){
        DiscussPost discussPost = discussPostService.findDiscussPost(discussPostId);
        //帖子
        model.addAttribute("post",discussPost);
        User user = userService.findUserById(discussPost.getUserId());
        //作者
        model.addAttribute("user",user);

        //点赞的数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);

        //点赞状态
        int likeStatus = hostHolder.getUser() == null?0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);


        //查评论信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        //回复：评论的评论
        //评论的列表
        List<Comment> commentList = commentService.findCommentsByEntity(CommunityConstant.ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());
        //评论的vol列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList){
                Map<String,Object> commentVo = new HashMap<>();
                //放入评论
                commentVo.put("comment",comment);
                //放入评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //点赞的数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                //点赞状态
                likeStatus = hostHolder.getUser() == null?0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);
                //回复的列表
                List<Comment> replayList = commentService.findCommentsByEntity(CommunityConstant.ENTITY_TYPE_COMMENT,
                        comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVolList = new ArrayList<>();
                if (replayList != null){
                    for (Comment r : replayList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",r);
                        //作者
                        replyVo.put("user",userService.findUserById(r.getUserId()));
                        //回复的目标
                        User target = r.getTargetId()==0?null:userService.findUserById(r.getTargetId());
                        replyVo.put("target",target);
                        //点赞的数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,r.getId());
                        replyVo.put("likeCount",likeCount);
                        //点赞状态
                        likeStatus = hostHolder.getUser() == null?0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,r.getId());
                        replyVo.put("likeStatus",likeStatus);
                        replyVolList.add(replyVo);
                    }
                }
                //回复添加到评论的显示map中
                commentVo.put("replys",replyVolList);
                //回复数量
                int replyCount = commentService.findCommentCount(CommunityConstant.ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                //将一个完整的vo添加到列表
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }

    //用户发布的帖子
    @RequestMapping(path = "/userPost",method = RequestMethod.GET)
    public String findUserDiscussPosts(Model model,Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/discuss/userPost");
        page.setRows(discussPostService.findDiscussPostRows(user.getId()));
        List<DiscussPost> list = discussPostService.findDiscussPosts(user.getId(),page.getOffset(),page.getLimit());
        model.addAttribute("discussPostCount",list.size());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost d : list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",d);
                int likeCount = (int) likeService.findEntityLikeCount(ENTITY_TYPE_POST,d.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPost",discussPosts);
        return "/site/my-post";
    }


}
