package fun.gengzi.core;

import fun.gengzi.aspect.LogRecord;
import lombok.NonNull;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <h1>日志注解字段资源</h1>
 * <p>
 * 判断当前方法上，是否有 logrecord 注解，有就解析参数
 *
 * @author Administrator
 * @date 2022/6/21 13:05
 */
public class LogRecordOperationSource {


    /**
     * 获取注解上配置的字段内容
     *
     * @param method      方法
     * @param targetClass 目标对象
     * @return
     */
    public Collection<LogRecordOps> computeLogRecordOperations(@NonNull Method method, @NonNull Class<?> targetClass) {
        // Don't allow no-public methods as required.
        // 不允许非公共方法
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        // 该方法可能在接口上，但我们需要来自目标类的属性。如果目标类为空，方法将不变.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // 如果我们正在处理带有泛型参数的方法，请找到原始方法。 移除泛型的参数方法
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        // 首先尝试的是目标类中的方法。
        return parseLogRecords(specificMethod);
    }

    /**
     * 解析所有的日志记录注解信息
     * <p>
     * 表示当前在此VM中运行的程序的注解元素。 该界面允许反射读取注释。
     * 通过这个接口方法返回的所有注释都是不可变并且可序列化。
     * 通过此接口的方法返回的阵列可以由呼叫者，而不会影响其他调用者返回阵列进行修改。
     *
     * @param annotatedElement
     * @return
     */
    private Collection<LogRecordOps> parseLogRecords(AnnotatedElement annotatedElement) {
        // 获取合并后logRecord 的注解
        Collection<LogRecord> logRecords = AnnotatedElementUtils.getAllMergedAnnotations(annotatedElement, LogRecord.class);
        Collection<LogRecordOps> result = null;
        if (!CollectionUtils.isEmpty(logRecords)) {
            result = lazyInit(result);
            for (LogRecord logRecord : logRecords) {
                result.add(parseLogRecord(annotatedElement, logRecord));
            }
        }
        return result;
    }

    /**
     * 解析单个日志记录注解信息
     *
     * @param annotatedElement
     * @param logRecord
     * @return
     */
    private LogRecordOps parseLogRecord(AnnotatedElement annotatedElement, LogRecord logRecord) {
        LogRecordOps logRecordOps = LogRecordOps.builder()
                .successLogTemplate(logRecord.success())
                .failLogTemplate(logRecord.fail())
                .bizKey(logRecord.prefix().concat("_").concat(logRecord.bizNo()))
                .bizNo(logRecord.bizNo())
                .operatorId(logRecord.operator())
                .category(StringUtils.isEmpty(logRecord.category()) ? logRecord.prefix() : logRecord.category())
                .detail(logRecord.detail())
                .condition(logRecord.condition())
                .build();
        validateLogRecordOperation(annotatedElement, logRecordOps);
        return logRecordOps;
    }

    /**
     * 校验日志注解必填
     *
     * @param ae
     * @param recordOps
     */
    private void validateLogRecordOperation(AnnotatedElement ae, LogRecordOps recordOps) {
        if (!StringUtils.hasText(recordOps.getSuccessLogTemplate()) && !StringUtils.hasText(recordOps.getFailLogTemplate())) {
            throw new IllegalStateException("Invalid logRecord annotation configuration on '" +
                    ae.toString() + "'. 'one of successTemplate and failLogTemplate' attribute must be set.");
        }
    }

    private Collection<LogRecordOps> lazyInit(Collection<LogRecordOps> ops) {
        return (ops != null ? ops : new ArrayList<>(1));
    }

}
