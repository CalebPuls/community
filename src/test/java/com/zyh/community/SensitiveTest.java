package com.zyh.community;
import com.zyh.community.until.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
/**
 * @author
 * @Description
 * @create 2022-06-02 20:54
 */
@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void  setSensitiveFilter(){
        String text = "澳门赌场，可赌发🤣博还可以嫖🤣娼🤣都可开🤣票🤣，但是不能🤣吸🤣毒🤣";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
