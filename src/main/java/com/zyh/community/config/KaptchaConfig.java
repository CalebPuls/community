package com.zyh.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.security.krb5.Config;
import sun.security.krb5.KrbException;

import java.util.Properties;

/**
 * @author
 * @Description
 * @create 2022-05-25 20:34
 */
@Configuration
public class KaptchaConfig {
    @Bean
    public Producer kaptchaProducer() throws KrbException {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789QWERTYUIOPASDFGHJKLZXCVBNM");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        com.google.code.kaptcha.util.Config config = new com.google.code.kaptcha.util.Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
