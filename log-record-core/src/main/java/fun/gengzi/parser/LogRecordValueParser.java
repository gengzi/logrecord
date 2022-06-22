package fun.gengzi.parser;

import fun.gengzi.function.IFunctionService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogRecordValueParser implements BeanFactoryAware {


    private static final Pattern pattern = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    private IFunctionService functionService;

    public void setFunctionService(IFunctionService functionService) {
        this.functionService = functionService;
    }

    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private final LogRecordExpressionEvaluator expressionEvaluator = new LogRecordExpressionEvaluator();


    /**
     * 处理自定义函数
     * <p>
     * <p>
     * 解析模板中，存在 { } 的内容，根据方法名称，去查找 函数工厂中是否存在，对应的方法，调用此方法，得到函数值
     *
     * @param spElTemplates spel模板
     * @param targetClass   目标对象
     * @param method        目标方法
     * @param args          方法参数
     * @return
     */
    protected Map<String, String> processBeforeExecuteFunctionTemplate(List<String> spElTemplates, Class<?> targetClass,
                                                                       Method method, Object[] args) {
        Map<String, String> functionNameAdnReturnValueMap = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, null, null, beanFactory);
        for (String templateStr : spElTemplates) {
            if (templateStr.contains("{")) {
                Matcher matcher = pattern.matcher(templateStr);
                // 匹配到，存在自定义函数
                if (matcher.find()) {
                    String expression = matcher.group(2);
                    if (expression.contains("#_ret") || expression.contains("#_errorMsg")) {
                        continue;
                    }
                    // 自定义函数名称
                    String functionName = matcher.group(1);
                    AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                    if (functionService.beforeFunction(functionName)) {
                        // 在方法执行前执行自定义函数
                        // 解析表达式，获取方法入参
                        String value = expressionEvaluator.parseExpression(templateStr, annotatedElementKey, evaluationContext);
                        // 调用自定义函数，获取响应结果
                        String result = getFunctionReturnValue(null, functionName, value);
                        functionNameAdnReturnValueMap.put(functionName, result);
                    }
                }
            }
        }
        return functionNameAdnReturnValueMap;
    }


    private String getFunctionReturnValue(Map<String, String> beforeFunctionNameAndReturnMap, String value,
                                          String functionName) {
        String functionReturnValue = "";
        if (beforeFunctionNameAndReturnMap != null) {
            beforeFunctionNameAndReturnMap.get(functionName);
        }
        if (StringUtils.isEmpty(functionReturnValue)) {
            functionReturnValue = functionService.apply(functionName, value);
        }
        return functionReturnValue;
    }


    public Map<String, String> processTemplate(Collection<String> templates, Object result, Class<?> targetClass, Method method, Object[] args, String errorMsg, Map<String, String> beforeFunctionNameAndReturnMap) {
        Map<String, String> expressionValues = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, result, errorMsg, beanFactory);
        for (String expressionTemplate : templates) {
            if (expressionTemplate.contains("{")) {
                Matcher matcher = pattern.matcher(expressionTemplate);
                StringBuffer parsedStr = new StringBuffer();
                while (matcher.find()) {
                    String expression = matcher.group(2);
                    AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                    String value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                    String functionReturnValue = getFunctionReturnValue(beforeFunctionNameAndReturnMap, value, matcher.group(1));
                    matcher.appendReplacement(parsedStr, functionReturnValue);
                }
                matcher.appendTail(parsedStr);
                expressionValues.put(expressionTemplate, parsedStr.toString());
            } else {
                expressionValues.put(expressionTemplate, expressionTemplate);
            }
        }
        return expressionValues;
    }
}
