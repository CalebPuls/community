package com.zyh.community.service;

import com.zyh.community.dao.MessageMapper;
import com.zyh.community.entity.Message;
import com.zyh.community.until.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author
 * @Description
 * @create 2022-06-05 15:12
 */
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }
    public List<Message> findLetter(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }
    public int addMessage(Message message){
        message.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(message.getContent())));
        return messageMapper.insertMessage(message);
    }
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }
    public int deleteLetter(int id){
        return messageMapper.deleteLetter(id);
    }
}
