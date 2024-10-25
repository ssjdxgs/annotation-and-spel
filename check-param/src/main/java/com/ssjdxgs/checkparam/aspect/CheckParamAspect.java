package com.ssjdxgs.checkparam.aspect;

import cn.hutool.core.util.ReflectUtil;
import com.ssjdxgs.checkparam.annotation.CheckParam;
import com.ssjdxgs.checkparam.annotation.CheckParams;
import com.ssjdxgs.checkparam.handler.DefaultCheckParamExpressionHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ssjdxgs
 */
@Aspect
@Configuration("checkParamAspect")
public class CheckParamAspect {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 表达式解析器
     */
    private final DefaultCheckParamExpressionHandler defaultCheckParamExpressionHandler =new DefaultCheckParamExpressionHandler();

    /**
     * 切点在controller包下的所有public方法
     */
    @Pointcut("execution(public * com.ssjdxgs..*(..))")
    public void checkParamPointcut(){}

    @Before("checkParamPointcut()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        Object[] args = joinPoint.getArgs();

        // 方法签名
        Signature signature = joinPoint.getSignature();
        Class<?> clazz = joinPoint.getTarget().getClass();
        // 获取的是代理类的method对象
        Method method = ((MethodSignature) signature).getMethod();
        // 这个方法才是目标对象上有注解的方法
        Method realMethod = clazz.getDeclaredMethod(signature.getName(), method.getParameterTypes());
        // 取出对应的注解
        GetMapping getMapping = realMethod.getAnnotation(GetMapping.class);
        PostMapping postMapping = realMethod.getAnnotation(PostMapping.class);
        // 如果两个注解都为null则可能是重写方法，尝试获取实现接口的方法
        // 示例代码的规则是controller实现的第一个接口为feign接口
        if (getMapping == null && postMapping == null){
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                realMethod = interfaces[0].getDeclaredMethod(signature.getName(), method.getParameterTypes());
                getMapping = realMethod.getAnnotation(GetMapping.class);
                postMapping = realMethod.getAnnotation(PostMapping.class);
            }
        }
        if (getMapping != null){
            getMappingMethod(args,realMethod);
        }else if (postMapping != null){
            postMappingMethod(args,realMethod);
        }
    }

    /**
     * 执行表达式来校验目标参数obj
     */
    private boolean check(Object obj,String expression) {
        Boolean b = defaultCheckParamExpressionHandler.parserValue(expression, obj);
        return b != null ? b : false;
    }

    /**
     * 通过调用对应bean来执行表达式来校验目标参数obj
     */
    private boolean checkBeanValue(Object obj,String expression){
        Boolean b = defaultCheckParamExpressionHandler.parseBeanValue(expression, obj, applicationContext);
        return b != null ? b : false;
    }

    private void getMappingMethod(Object[] args,Method realMethod) throws Exception{
        // 取出对应的注解
        int num = -1;
        Annotation[][] parameterAnnotations = realMethod.getParameterAnnotations();

        for (Annotation[] annotations : parameterAnnotations) {
            num = num + 1;
            for (Annotation annotation : annotations) {
                //获取注解名
                Object obj = args[num];
                if(annotation instanceof CheckParam){
                    parseCheckParam(annotation,obj);
                }else if (annotation instanceof CheckParams){
                    CheckParam[] checkParams = ((CheckParams) annotation).value();
                    for (CheckParam checkParam : checkParams) {
                        parseCheckParam(checkParam, obj);
                    }
                }
            }
        }
    }


    private void postMappingMethod(Object[] args,Method realMethod) throws Exception{
        int num = -1;
        // 1、取出全部属性的所有的注解,分为一个二维数组
        Annotation[][] parameterAnnotations = realMethod.getParameterAnnotations();

        // 2、遍历二维数组拿到每一个属性的全部注解
        for (Annotation[] annotations : parameterAnnotations) {
            num = num + 1;
            boolean hasRequestBody = false;
            // 3、获取属性值
            Object obj = args[num];
            // 4、遍历该属性的注解
            for (Annotation annotation : annotations) {
                // 5、对于post请求,注解有两中情况,@RequestParam和@RequestBody
                // 如果@RequestParam属性存在@CheckParam注解则直接解析
                if(annotation instanceof CheckParam){
                    parseCheckParam(annotation,obj);
                }else if (annotation instanceof RequestBody){
                    // 6、如果是@RequestBody注解，则需要进步解析请求体内部属性是否有@CheckParam注解并解析
                    // 但是因为有可能@RequestBody注解和@RequestParam注解同时存在，所以此处先进行标记
                    hasRequestBody = true;
                }else if (annotation instanceof CheckParams){
                    CheckParam[] checkParams = ((CheckParams) annotation).value();
                    for (CheckParam checkParam : checkParams) {
                        parseCheckParam(checkParam, obj);
                    }
                }
            }
            // 7、解析@RequestBody注解内部属性
            if (hasRequestBody){
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    // 8、属性的注解有可能是@CheckParam注解,也有可能是@CheckParams注解,二者不会共存
                    CheckParam checkParam = field.getAnnotation(CheckParam.class);
                    if (checkParam != null){
                        parseCheckParam(checkParam, ReflectUtil.getFieldValue(obj,field));
                    }
                    CheckParams checkParams = field.getAnnotation(CheckParams.class);
                    if (checkParams != null) {
                        CheckParam[] checkParams1 = checkParams.value();
                        for (CheckParam checkParam1 : checkParams1) {
                            parseCheckParam(checkParam1, ReflectUtil.getFieldValue(obj, field));
                        }
                    }
                }
            }
        }
    }

    private void parseCheckParam(Annotation annotation,Object obj) throws Exception{
        CheckParam checkParam =  (CheckParam)annotation;
        String expression = checkParam.value();
        String beanExpression = checkParam.beanMethod();
        String message = checkParam.message();
        if (expression != null && !expression.isEmpty()) {
            if (!check(obj, expression)) {
                throw new Exception(message);
            }
        }else if (beanExpression != null && !beanExpression.isEmpty()){
            if (!checkBeanValue(obj, beanExpression)) {
                throw new Exception(message);
            }
        }
    }
}

