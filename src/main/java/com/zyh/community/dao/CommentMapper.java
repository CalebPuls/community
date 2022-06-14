package com.zyh.community.dao;

import com.zyh.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author
 * @Description 评论的mapper
 * @create 2022-06-03 21:10
 */
@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType,
                                         @Param("entityId")int entityId,
                                         @Param("offset")int offset,
                                         @Param("limit")int limit);
    List<Comment> selectCommentsByUserId(@Param("userId") int userId,
                                         @Param("entityType") int entityType,
                                         @Param("offset")int offset,
                                         @Param("limit")int limit);
    int selectCountByUserId(@Param("userId") int userId,@Param("entityType") int entityType);
    int selectCountByEntity(@Param("entityType")int entityType,@Param("entityId")int entityId);

    int insertComment(Comment comment);
}
