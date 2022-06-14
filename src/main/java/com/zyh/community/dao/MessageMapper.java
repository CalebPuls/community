package com.zyh.community.dao;

import com.zyh.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author
 * @Description
 * @create 2022-06-05 14:20
 */
@Mapper
public interface MessageMapper {
    //查询当前用户的会话列表,每个会话只返回一条最新的私信
    List<Message> selectConversations(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);
    //查询当前用户的会话数量
    int selectConversationCount(@Param("userId")int userId);
    //查询某个会话所包含私信列表
    List<Message> selectLetters(@Param("conversationId") String conversationId,@Param("offset") int offset,@Param("limit") int limit);
    //查询某个会话包含的私信数量
    int selectLetterCount(@Param("conversationId")String conversionId);
    //查询未读私信数量
    int selectLetterUnreadCount(@Param("userId")int userId,@Param("conversationId")String conversionId);
    //新增消息
    int insertMessage(Message message);
    //更改消息状态
    int updateStatus(@Param("ids")List<Integer> ids,@Param("status")int status);
    //删除消息
    int deleteLetter(@Param("id")int id);


}
