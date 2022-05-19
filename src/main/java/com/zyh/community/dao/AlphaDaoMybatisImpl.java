package com.zyh.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author
 * @Description
 * @create 2022-05-11 14:05
 */

@Repository
@Primary
public class AlphaDaoMybatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "MyBatis";
    }
}
