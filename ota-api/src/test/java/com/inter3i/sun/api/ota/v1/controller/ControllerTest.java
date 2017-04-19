/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/30/16 11:07 AM
 *   Description:
 *
 */

package com.inter3i.sun.api.ota.v1.controller;

import com.inter3i.sun.api.ota.v1.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ControllerTest {

    @Autowired
    private TestController testController;

    @Test
    public void test() {
        String body = this.testController.test();
        assertThat(body).isNotEmpty();

        List<Integer> list = this.testController.testList();
        assertThat(list).hasSize(3)
                        .containsAll(Arrays.asList(1, 2, 3));
    }
}
