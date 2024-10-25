package com.ssjdxgs.checkparam.demo;

import com.ssjdxgs.checkparam.annotation.CheckParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ssjdxgs
 */
public interface TestFeign {

    @PostMapping("/checkFeign")
    void checkFeign(@RequestParam @CheckParam("notNull()") String username,
                    @RequestBody @CheckParam(value = "notEmpty()",message = "数组不能为空") List<Integer> numList);
}
