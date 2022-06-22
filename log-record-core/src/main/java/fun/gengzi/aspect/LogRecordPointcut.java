package fun.gengzi.aspect;

import fun.gengzi.core.LogRecordOperationSource;
import lombok.NonNull;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 切入点表达式
 *
 * @author Administrator
 *
 */
public class LogRecordPointcut extends StaticMethodMatcherPointcut implements Serializable {
    // LogRecord的解析类
    private LogRecordOperationSource logRecordOperationSource;
    
    @Override
    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
          // 解析 这个 method 上有没有 @LogRecordAnnotation 注解，有的话会解析出来注解上的各个参数
        return !CollectionUtils.isEmpty(logRecordOperationSource.computeLogRecordOperations(method, targetClass));
    }

    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }
}