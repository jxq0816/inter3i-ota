package com.inter3i.sun.api.ota.v1.controller;

import com.inter3i.sun.api.ota.v1.service.ServiceFactory;
import com.inter3i.sun.api.ota.v1.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController("/test")
public class TestController {

    private TestService testService;

    public TestController() {
        this.testService = ServiceFactory.testService();
    }

    @RequestMapping("/text")
    public @ResponseBody String test() {
        return testService.test();
    }

    @RequestMapping("/list")
    public @ResponseBody List<Integer> testList() {
        return Arrays.asList(1, 2, 3);
    }
}
