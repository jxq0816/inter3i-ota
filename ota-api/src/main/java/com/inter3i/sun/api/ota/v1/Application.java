/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:01 PM
 *   Description:
 *
 */

package com.inter3i.sun.api.ota.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
//部署到Tomcat服务器时，禁用mongo默认配置
@SpringBootApplication(exclude = {MongoAutoConfiguration.class,MongoDataAutoConfiguration.class})
//在部署到外部的Tomcat时，需要将classpath的引入文件去掉，因为在web.xml已经配置过一次了
//@ImportResource("classpath:dispatcher-servlet.xml")
public class Application{
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}