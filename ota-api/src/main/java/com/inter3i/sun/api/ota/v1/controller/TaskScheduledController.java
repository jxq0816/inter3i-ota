package com.inter3i.sun.api.ota.v1.controller;

import com.inter3i.sun.api.ota.v1.service.TaskScheduledService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by boxiaotong on 2017/6/5.
 */
@Controller
@RequestMapping("task")
public class TaskScheduledController {
    @Autowired
    private TaskScheduledService taskScheduledService;

    @ResponseBody
    @RequestMapping(value = {"insert",""})
    public String insert(String jobType,String cacheName) {
        String rs=taskScheduledService.insert(jobType,cacheName);
        return rs;
    }

    @ResponseBody
    @RequestMapping("update")
    public String update(String jobType,String cacheName,boolean status) {
        String rs=taskScheduledService.updateOne(jobType,cacheName,status);
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = {"list",""})
    public Set list() {
        Set set= new HashSet<Document>();
        Iterator<Document> iterator=taskScheduledService.findList();
        while (iterator.hasNext()) {
            Document document=iterator.next();
            set.add(document);
        }
        return set;
    }
}
