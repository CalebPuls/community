package com.zyh.community.service;

import com.zyh.community.until.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author
 * @Description
 * @create 2022-06-09 9:49
 */
@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId,int entityUserId,int entityType,int entityId){
        //单独对某个实体的点赞，因为加入了记录用户点赞数的功能，
        // 所以要变成一个事务
//        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
//
//        boolean isMember = redisTemplate.opsForSet().isMember(entityKey,userId);
//        if(isMember){
//            redisTemplate.opsForSet().remove(entityKey,userId);
//        }else {
//            redisTemplate.opsForSet().add(entityKey,userId);
//        }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMmber = redisTemplate.opsForSet().isMember(entityKey,userId);

                operations.multi();
                if (isMmber){
                    operations.opsForSet().remove(entityKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else {
                    operations.opsForSet().add(entityKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    //查询某一实体点赞数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityKey);
    }

    //查询某用户是否点赞
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        if(redisTemplate.opsForSet().isMember(entityKey,userId)){
            return 1;
        }else {
            return 0;
        }
    }

    //查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer res = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return res==null?0:res.intValue();
    }
}
