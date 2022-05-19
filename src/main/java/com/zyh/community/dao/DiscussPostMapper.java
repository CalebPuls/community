package com.zyh.community.dao;

import com.zyh.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author
 * @Description
 * @create 2022-05-16 17:40
 */
@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);
    //@Param()用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须要别名
    int selectDiscussPostRow(@Param("userId") int userId);
}
