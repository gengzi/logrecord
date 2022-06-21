package fun.gengzi.core;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * * 把方法的参数都放到 SpEL 解析的 RootObject 中
 * * 把 LogRecordContext 中的变量都放到 RootObject 中
 * * 把方法的返回值和 ErrorMsg 都放到 RootObject 中
 *
 *
 *
 * EvaluationContext在评估表达式以解析属性、方法或字段并帮助执行类型转换时，使用该接口。Spring 提供了两种实现。
 *
 * SimpleEvaluationContext：针对不需要完整范围的 SpEL 语言语法并应受到有意义限制的表达式类别，公开了基本 SpEL 语言功能和配置选项的子集。示例包括但不限于数据绑定表达式和基于属性的过滤器。
 *
 * StandardEvaluationContext：公开全套 SpEL 语言功能和配置选项。您可以使用它来指定默认根对象并配置每个可用的评估相关策略。
 *
 *
 */
public class LogRecordEvaluationContext extends MethodBasedEvaluationContext {

    public LogRecordEvaluationContext(Object rootObject, Method method, Object[] arguments,
                                      ParameterNameDiscoverer parameterNameDiscoverer, Object ret, String errorMsg) {
       //把方法的参数都放到 SpEL 解析的 RootObject 中
       super(rootObject, method, arguments, parameterNameDiscoverer);
       //把 LogRecordContext 中的变量都放到 RootObject 中
        Map<String, Object> variables = LogRecordContext.getVariables();
        if (variables != null && variables.size() > 0) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                setVariable(entry.getKey(), entry.getValue());
            }
        }
        //把方法的返回值和 ErrorMsg 都放到 RootObject 中
        setVariable("_ret", ret);
        setVariable("_errorMsg", errorMsg);
    }
}