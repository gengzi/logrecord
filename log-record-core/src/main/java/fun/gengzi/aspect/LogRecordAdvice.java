package fun.gengzi.aspect;

import fun.gengzi.core.LogRecordContext;
import fun.gengzi.core.LogRecordOperationSource;
import fun.gengzi.core.LogRecordOps;
import fun.gengzi.core.MethodExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <h1> </h1>
 *
 * @author Administrator
 * @date 2022/6/21 13:52
 */
@Slf4j
public class LogRecordAdvice implements MethodInterceptor {


    @Autowired
    private LogRecordOperationSource logRecordOperationSource;


    /**
     * 切面增强逻辑
     *
     * @param invocation 方法调用信息
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        // 记录日志
        return execute(invocation, invocation.getThis(), method, invocation.getArguments());
    }

    /**
     * 具体处理方法
     * <p>
     * // 1,方法执行前，模板解析，解析spel 表达式
     * // 2,自定义函数解析，参数封装
     * <p>
     * <p>
     * // 3,方法执行后，异常，打印错误模板内容，正常，打印正确模板内容
     * // 4,记录日志内存，存储
     *
     * @param invoker 方法调用信息
     * @param target  目标对象
     * @param method  方法信息
     * @param args    方法参数信息
     * @return
     * @throws Throwable
     */
    private Object execute(MethodInvocation invoker, Object target, Method method, Object[] args) throws Throwable {
        // 获取目标对象
        Class<?> targetClass = getTargetClass(target);
        Object ret = null;
        MethodExecuteResult methodExecuteResult = new MethodExecuteResult(true, null, "");
        // 先设置一个空位置
        LogRecordContext.putEmptySpan();
        Collection<LogRecordOps> operations = new ArrayList<>();
        Map<String, String> functionNameAndReturnMap = new HashMap<>();
        try {
            // 获取
            operations = logRecordOperationSource.computeLogRecordOperations(method, targetClass);
            // 根据参数获取 spel 模板
            List<String> spElTemplates = getBeforeExecuteFunctionTemplate(operations);
            //业务逻辑执行前的自定义函数解析
            functionNameAndReturnMap = processBeforeExecuteFunctionTemplate(spElTemplates, targetClass, method, args);
        } catch (Exception e) {
            log.error("log record parse before function exception", e);
        }
        try {
            // 放行
            ret = invoker.proceed();
        } catch (Exception e) {
            methodExecuteResult = new MethodExecuteResult(false, e, e.getMessage());
        }
        try {
            if (!CollectionUtils.isEmpty(operations)) {
                recordExecute(ret, method, args, operations, targetClass,
                        methodExecuteResult.isSuccess(), methodExecuteResult.getErrorMsg(), functionNameAndReturnMap);
            }
        } catch (Exception t) {
            //记录日志错误不要影响业务
            log.error("log record parse exception", t);
        } finally {
            // 释放资源
            LogRecordContext.clear();
        }
        if (methodExecuteResult.throwable != null) {
            throw methodExecuteResult.throwable;
        }
        return ret;
    }

    /**
     * 获取目标对象的class
     *
     * @param target
     * @return
     */
    private Class getTargetClass(Object target) {
        return target.getClass();
    }

    private List<String> getBeforeExecuteFunctionTemplate(Collection<LogRecordOps> logRecordOps){

//        logRecordOps.stream().collect()
    }
}
