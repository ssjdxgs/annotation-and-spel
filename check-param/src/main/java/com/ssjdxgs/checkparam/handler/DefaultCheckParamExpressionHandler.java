package com.ssjdxgs.checkparam.handler;


import com.ssjdxgs.checkparam.check.CheckParamRoot;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author ssjdxgs
 */
public class DefaultCheckParamExpressionHandler {
    /**
     * 初始化表达式解析器
     */
    private ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * 创建表达式上下文
     */
    public final EvaluationContext createEvaluationContext(Object value) {
        //设置参数
        CheckParamRoot root = this.createCheckParamExpressionRoot(value);
        StandardEvaluationContext ctx = this.createEvaluationContextInternal();
        ctx.setRootObject(root);
        return ctx;
    }

    /**
     * 创建表达式上下文
     */
    public final EvaluationContext createEvaluationContext(Object obj, ApplicationContext applicationContext) {
        //设置bean解析器
        StandardEvaluationContext ctx = this.createEvaluationContextInternal();
        ctx.setBeanResolver(new BeanFactoryResolver(applicationContext));
        ctx.setVariable("obj",obj);
        return ctx;
    }

    private StandardEvaluationContext createEvaluationContextInternal() {
        return new StandardEvaluationContext();
    }

    private CheckParamRoot createCheckParamExpressionRoot(Object value) {
        CheckParamRoot root =new CheckParamRoot(value);
        return root;
    }


    /**
     * 解析表达式
     */
    public Boolean parserValue(String expressionStr,Object value){
        Expression expression =expressionParser.parseExpression(expressionStr);
        return evaluateAsBoolean(expression,createEvaluationContext(value));
    }

    /**
     * 解析表达式
     */
    public Boolean parseBeanValue(String expressionStr,Object obj, ApplicationContext applicationContext){
        Expression expression =expressionParser.parseExpression(expressionStr);
        return evaluateAsBoolean(expression,createEvaluationContext(obj,applicationContext));
    }


    /**
     * 处理返回值
     */
    public static Boolean evaluateAsBoolean(Expression expr, EvaluationContext ctx)  {
        try {
            return (Boolean) expr.getValue(ctx, Boolean.class);
        } catch (EvaluationException var3) {
            throw new RuntimeException("Failed to evaluate expression '" + expr.getExpressionString() + "'");
        }
    }
}
