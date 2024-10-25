package com.ssjdxgs.checkparam.check;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author ssjdxgs
 */
@Component("checkParamBean")
public class CheckParamBean {
    /**
     * 密码必须为8-12为英文或数字
     */
    public boolean checkPassword(String password) {
        if (ObjectUtils.isEmpty(password)) {
            return false;
        }
        return password.matches("^[a-zA-Z0-9]{8,12}$");
    }
}
