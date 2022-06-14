package com.zyh.community.service;

import com.zyh.community.dao.CommentMapper;
import com.zyh.community.dao.DiscussPostMapper;
import com.zyh.community.entity.Comment;
import com.zyh.community.until.CommunityConstant;
import com.zyh.community.until.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author
 * @Description
 * @create 2022-06-03 21:21
 */
@Service
public class CommentService implements CommunityConstant{
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }
    public List<Comment> findCommentsByUserId(int userId,int entityType,int offset,int limit){
        return commentMapper.selectCommentsByUserId(userId,entityType,offset,limit);
    }
    public int findCommentCountByUserId(int userId,int entityType){
        return commentMapper.selectCountByUserId(userId,entityType);
    }

    //添加评论并且更新评论数量，这是在一个事务中处理完成
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int addComment(Comment comment){
        if (comment == null){
            throw new  IllegalArgumentException("参数不能为空");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int row = commentMapper.insertComment(comment);
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(ENTITY_TYPE_POST,comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(),count);
        }
        return row;
    }
}
