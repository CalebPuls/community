package com.zyh.community.service;

import com.zyh.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author
 * @Description
 * @create 2022-05-11 14:24
 */
//@Scope("prototype")
@Service
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    public AlphaService(){

    }
    @PostConstruct
    public void inial(){

    }
    @PreDestroy
    public void destroy(){

    }

    public String select(){
        return alphaDao.select();
    }
}
