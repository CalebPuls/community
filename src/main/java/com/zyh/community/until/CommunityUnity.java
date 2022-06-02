package com.zyh.community.until;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author
 * @Description
 * @create 2022-05-20 11:15
 */
public class CommunityUnity {
    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密
    public static String md5(String key){
        if (StringUtils.isAllBlank(key))return null;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
