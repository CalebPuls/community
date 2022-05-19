package com.zyh.community;

import com.zyh.community.controller.Alpha;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
		Alpha alpha = new Alpha();

	}

}
