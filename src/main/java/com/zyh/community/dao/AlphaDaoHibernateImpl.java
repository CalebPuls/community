package com.zyh.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author
 * @Description
 * @create 2022-05-11 13:54
 */

@Repository("Hibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
