package fun.gengzi.parser;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;


public class LogRecordValueParser implements BeanFactoryAware {

    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    private final LogRecordExpressionEvaluator expressionEvaluator = new LogRecordExpressionEvaluator();



    /**
     * 处理自定义函数
     *
     * @param spElTemplates  spel模板
     * @param targetClass 目标对象
     * @param method  目标方法
     * @param args 方法参数
     * @return
     */
    protected Map<String, String> processBeforeExecuteFunctionTemplate(List<String> spElTemplates, Class<?> targetClass,
                                                                       Method method, Object[] args){

        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, null, null, beanFactory);
        evaluationContext.

    }
}
