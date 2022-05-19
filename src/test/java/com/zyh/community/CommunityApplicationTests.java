package com.zyh.community;

import com.zyh.community.dao.AlphaDao;
import com.zyh.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;


@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	void testApplicationContext() {
		System.out.println(applicationContext);

		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		AlphaDao alphaDao1 =  applicationContext.getBean("Hibernate",AlphaDao.class);
		System.out.println(alphaDao1.select());
	}

	@Test
	public void testBeanManger(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
	}

	@Test
	public void testConfig(){
		SimpleDateFormat simpleDateFormat =
				applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
	@Qualifier("Hibernate")
	private AlphaDao alphaDao;
	@Test
	public void testDI(){
		System.out.println(alphaDao);
	}

}
