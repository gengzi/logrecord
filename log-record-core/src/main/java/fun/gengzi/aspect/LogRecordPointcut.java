package fun.gengzi.aspect;

import fun.gengzi.core.LogRecordOperationSource;
import lombok.NonNull;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * <h1>切入点表达式</h1>
 * <p>
 * 用于判断当前方法是否被日志记录注解修饰
 *
 * @author Administrator
 * @date 2022年6月22日14:34:56
 */
public class LogRecordPointcut extends StaticMethodMatcherPointcut implements Serializable {
    // LogRecord的解析类
    private LogRecordOperationSource logRecordOperationSource;

    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

    /**
     * 匹配方法
     *
     * @param method      拦截的方法
     * @param targetClass 目标对象
     * @return
     */
    @Override
    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
        // 解析 这个 method 上有没有 @LogRecordAnnotation 注解，有的话会解析出来注解上的各个参数
        return !CollectionUtils.isEmpty(logRecordOperationSource.computeLogRecordOperations(method, targetClass));
    }

}