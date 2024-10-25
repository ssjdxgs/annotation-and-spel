package com.ssjdxgs.checkparam.check;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * @author ssjdxgs
 * 所有方法都是返回boolean类型
 * true表示校验通过
 * false表示校验不通过,将会抛出异常
 * 通过注解的message属性可以设置校验不通过时的提示信息
 * 后续处理逻辑在CheckParamAspect中
 */
@Data
@AllArgsConstructor
public class CheckParamRoot {

    private Object obj;

    public boolean notNull(){
        return obj != null;
    }

    public boolean notEmpty(){
        return !ObjectUtils.isEmpty(obj);
    }

    /**
     * obj是否为大陆手机号
     */
    public boolean checkPhone() {
        if (obj == null) {
            return false;
        } else if (obj instanceof String) {
            return ((String) obj).matches("^1[3-9]\\d{9}$");
        } else {
            return false;
        }
    }
}
