package com.zyh.community.until;

import com.zyh.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author
 * @Description 持有用户信息，代替session对象（线程安全）
 * @create 2022-05-30 21:18
 */
@Component
public class HostHolder {
    private  ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        this.users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
