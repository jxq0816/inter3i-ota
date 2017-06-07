package com.inter3i.sun.api.ota.v1.service;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by boxiaotong on 2017/6/7.
 */
public class TaskScheduledServiceTest {
    @Test
    public void insert(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dispatcher-servlet.xml");
        TaskScheduledService taskScheduledService= context.getBean(TaskScheduledService.class);
        String rs=taskScheduledService.insert("0","cache01");
        System.out.println(rs);
        rs=taskScheduledService.insert("0","cache02");
        System.out.println(rs);
        rs=taskScheduledService.insert("0","cache03");
        System.out.println(rs);
        rs=taskScheduledService.insert("0","cache04");
        System.out.println(rs);

        rs=taskScheduledService.insert("1","cache01");
        System.out.println(rs);
        rs=taskScheduledService.insert("1","cache02");
        System.out.println(rs);
        rs=taskScheduledService.insert("1","cache03");
        System.out.println(rs);
        rs=taskScheduledService.insert("1","cache04");
        System.out.println(rs);
    }
    @Test
    public void update(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dispatcher-servlet.xml");
        TaskScheduledService taskScheduledService= context.getBean(TaskScheduledService.class);
        taskScheduledService.updateOne("1","cache04",false);
    }
}
