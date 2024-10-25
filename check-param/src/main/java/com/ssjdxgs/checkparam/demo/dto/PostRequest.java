package com.ssjdxgs.checkparam.demo.dto;

import com.ssjdxgs.checkparam.annotation.CheckParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ssjdxgs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequest {

    @CheckParam("notEmpty()")
    @CheckParam(value = "checkPhone()",message = "手机号不合法")
    private String phone;

    @CheckParam(beanMethod = "@checkParamBean.checkPassword(#obj)",message = "密码不合法")
    private String password;
}
