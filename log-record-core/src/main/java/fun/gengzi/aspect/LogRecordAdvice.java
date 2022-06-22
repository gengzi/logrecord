package fun.gengzi.aspect;

import fun.gengzi.core.LogRecordContext;
import fun.gengzi.core.LogRecordOperationSource;
import fun.gengzi.core.LogRecordOps;
import fun.gengzi.core.MethodExecuteResult;
import fun.gengzi.parser.LogRecordValueParser;
import fun.gengzi.save.ILogRecordService;
import fun.gengzi.userinfo.IOperatorGetService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <h1> </h1>
 *
 * @author Administrator
 * @date 2022/6/21 13:52
 */
@Slf4j
public class LogRecordAdvice extends LogRecordValueParser implements MethodInterceptor {


    private LogRecordOperationSource logRecordOperationSource;


    private IOperatorGetService iOperatorGetService;

    private ILogRecordService iLogRecordService;

    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

    public void setiOperatorGetService(IOperatorGetService iOperatorGetService) {
        this.iOperatorGetService = iOperatorGetService;
    }

    public void setiLogRecordService(ILogRecordService iLogRecordService) {
        this.iLogRecordService = iLogRecordService;
    }

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
     * 日志记录，实现日志存储
     *
     * @param ret
     * @param method
     * @param args
     * @param isSuccess
     * @param errorMsg
     * @param functionNameAndReturnMap
     */
    private void recordExecute(Object ret, Method method, Object[] args, Collection<LogRecordOps> logRecordOps, Class<?> targetClass
            , boolean isSuccess, String errorMsg, Map<String, String> functionNameAndReturnMap) {

        // 判断当前方法执行成功，还是失败
        for (LogRecordOps ops : logRecordOps) {
            String action = getActionContent(isSuccess, ops);
            if (StringUtils.isEmpty(action)) {
                continue;
            }
            // 成功，记录成功信息              // 失败，记录失败信息
            List<String> spELTemplates = getSpELTemplates(ops, action);

            String operatorIdFromServiceAndPutTemplate = getOperatorIdFromServiceAndPutTemplate(ops, spELTemplates);

            // 根据解析到的配置log的选项，解析spel 表达式，存在自定义函数，执行自定义函数，获取结果
            Map<String, String> expressionValues = processTemplate(spELTemplates, ret, targetClass, method, args, errorMsg, functionNameAndReturnMap);
            // 重新拼装 log 选项，将原有配置，替换为正常展示的文本
            LogRecordOps logRecord = LogRecordOps.builder()
                    .bizKey(expressionValues.get(ops.getBizKey()))
                    .bizNo(expressionValues.get(ops.getBizNo()))
                    .operatorId(expressionValues.get(ops.getOperatorId()))
                    .category(ops.getCategory())
                    .detail(expressionValues.get(ops.getDetail()))
                    .successLogTemplate(expressionValues.get(action))
                    .build();

            // action 为空, 不记录日志
            if (StringUtils.isEmpty(logRecord.getSuccessLogTemplate())){
                continue;
            }
            // save log 需要新开事务, 失败日志不能因为事务回滚而丢失
            iLogRecordService.record(logRecord);

        }


        // 调用日志记录接口，实现日志记录
    }

    private String getOperatorIdFromServiceAndPutTemplate(LogRecordOps operation, List<String> spELTemplates) {
        String realOperatorId = "";
        if (StringUtils.isEmpty(operation.getOperatorId())) {
            realOperatorId = iOperatorGetService.getUser().getOperatorName();
            if (StringUtils.isEmpty(realOperatorId)) {
                throw new IllegalArgumentException("[LogRecord] operator is null");
            }
        } else {
            spELTemplates.add(operation.getOperatorId());
        }
        return realOperatorId;
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


    /**
     * 获取执行方法前的，spel 模板
     *
     * @param logRecordOps 方法注解的信息
     * @return
     */
    private List<String> getBeforeExecuteFunctionTemplate(Collection<LogRecordOps> logRecordOps) {
        ArrayList<String> spelTemplateAll = new ArrayList<>();
        // 获取模板
        for (LogRecordOps ops : logRecordOps) {
            spelTemplateAll.addAll(getSpELTemplates(ops, ops.getSuccessLogTemplate()));
        }
        return spelTemplateAll;
    }

    private List<String> getSpELTemplates(LogRecordOps operation, String action) {
        ArrayList<String> spelTemplate = new ArrayList<>();
        spelTemplate.add(action);
        spelTemplate.add(operation.getBizNo());
        spelTemplate.add(operation.getOperatorId());
        spelTemplate.add(operation.getCondition());
        if (!StringUtils.isEmpty(operation.getCondition())) {
            spelTemplate.add(operation.getCondition());
        }
        return spelTemplate;
    }

    private String getActionContent(boolean success, LogRecordOps operation) {
        String action = "";
        if (success) {
            action = operation.getSuccessLogTemplate();
        } else {
            action = operation.getFailLogTemplate();
        }
        return action;
    }

}
