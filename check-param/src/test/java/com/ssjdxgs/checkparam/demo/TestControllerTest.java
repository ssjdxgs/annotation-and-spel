package com.ssjdxgs.checkparam.demo;

import com.ssjdxgs.Application;
import com.ssjdxgs.checkparam.demo.dto.PostRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author ssjdxgs
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
class TestControllerTest {

    @Resource
    private TestController testController;

    @Test
    void checkGet() {
        String str = "";
        testController.checkGet(str);
    }

    @Test
    void checkPost() {
        String str = "";
        PostRequest postRequest = PostRequest.builder()
                .phone("111")
                .password("11111")
                .build();
        testController.checkPost(str,postRequest);
    }

    @Test
    void checkFeign() {
        testController.checkFeign("111",null);
    }
}