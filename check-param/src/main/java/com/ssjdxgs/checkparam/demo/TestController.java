package com.ssjdxgs.checkparam.demo;

import com.ssjdxgs.checkparam.annotation.CheckParam;
import com.ssjdxgs.checkparam.demo.dto.PostRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ssjdxgs
 */
@RestController()
@RequestMapping("/test")
public class TestController implements TestFeign {


    @GetMapping("/checkGet")
    public void checkGet(@RequestParam @CheckParam("notNull()") @CheckParam("notEmpty()") String str){
        System.out.println(str);
    }

    @PatchMapping("/checkPost")
    public void checkPost(@RequestParam @CheckParam("notNull()") @CheckParam("notEmpty()") String str,
                          @RequestBody @CheckParam("notNull()") PostRequest postRequest){
        System.out.println(str);
        System.out.println(postRequest);
    }
    @Override
    public void checkFeign(String username, List<Integer> numList) {
        System.out.println(username);
        System.out.println(numList);
    }
}