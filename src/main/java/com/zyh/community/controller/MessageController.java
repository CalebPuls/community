package com.zyh.community.controller;

import com.zyh.community.entity.Message;
import com.zyh.community.entity.Page;
import com.zyh.community.entity.User;
import com.zyh.community.service.MessageService;
import com.zyh.community.service.UserService;
import com.zyh.community.until.CommunityUnity;
import com.zyh.community.until.HostHolder;
import org.apache.ibatis.annotations.Mapper;
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
 * @create 2022-06-05 15:18
 */
@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    //私信列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        page.setPath("/letter/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));
        //查询会话列表
        List<Message> conversationList =
                messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for (Message m : conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",m);
                map.put("letterCount",messageService.findLetterCount(m.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),m.getConversationId()));
                int targetId = user.getId() == m.getFromId() ? m.getToId():m.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    //查询私信详情
    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //设置分页
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        List<Message> letterList = messageService.findLetter(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if (letterList!=null){
            for (Message m : letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("fromUser",userService.findUserById(m.getFromId()));
                map.put("letter",m);
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);
        //私信目标
        int targetId = getTargetId(conversationId);
        model.addAttribute("target",userService.findUserById(targetId));
        //设置为已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    //发送私信
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User fromUser = hostHolder.getUser();
        User toUser = userService.findUserByName(toName);
        if (toUser == null){
            return CommunityUnity.getJSONString(1,"目标用户不存在");
        }
        Message message = new Message();
        message.setContent(content);
        String conversationId = fromUser.getId()>toUser.getId()?
                toUser.getId()+"_"+fromUser.getId():fromUser.getId()+"_"+toUser.getId();
        message.setConversaionId(conversationId);
        message.setCreateTime(new Date());
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());

        messageService.addMessage(message);

        return CommunityUnity.getJSONString(0);
    }
    @RequestMapping(path = "/letter/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deletMessage(int id){
        messageService.deleteLetter(id);
        return CommunityUnity.getJSONString(0);
    }



    private int getTargetId(String conversationId){
        String[] strings = conversationId.split("_");
        User user = hostHolder.getUser();
        return user.getId() == Integer.parseInt(strings[0])?Integer.parseInt(strings[1]):
                Integer.parseInt(strings[0]);
    }

    private List<Integer> getLetterIds(List<Message> list){
        List<Integer> ids = new ArrayList<>();
        if (list!=null){
            for (Message message : list){
                if (hostHolder.getUser().getId() == message.getToId()
                        && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
}
