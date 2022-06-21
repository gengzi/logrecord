package fun.gengzi.parser;

import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务日志spel 表达式
 *
 * @author Administrator
 * @date 2022/6/21 13:52
 */
public class LogRecordExpressionEvaluator extends CachedExpressionEvaluator {

    // SpEL 会解析成一个 Expression 表达式，然后根据传入的 Object 获取到对应的值，
    // 所以 expressionCache 是为了缓存方法、表达式和 SpEL 的 Expression 的对应关系，
    // 让方法注解上添加的 SpEL 表达式只解析一次
    private Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    // 是为了缓存传入到 Expression 表达式的 Object
    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);

    /**
     * 解析表达式
     *
     * @param conditionExpression
     * @param methodKey
     * @param evalContext
     * @return
     */
    public String parseExpression(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return getExpression(this.expressionCache, methodKey, conditionExpression).getValue(evalContext, String.class);
    }
}