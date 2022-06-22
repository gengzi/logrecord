package fun.gengzi.autocinfig;

import fun.gengzi.aspect.LogRecordAdvice;
import fun.gengzi.aspect.LogRecordPointcut;
import fun.gengzi.aspect.LogRecordPointcutAdvisor;
import fun.gengzi.core.LogRecordOperationSource;
import fun.gengzi.function.*;
import fun.gengzi.save.DefaultLogRecordServiceImpl;
import fun.gengzi.save.ILogRecordService;
import fun.gengzi.userinfo.DefaultOperatorGetServiceImpl;
import fun.gengzi.userinfo.IOperatorGetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * <h1>装配核心类 </h1>
 *
 * @author Administrator
 * @date 2022/6/22 11:04
 */
@Slf4j
@Configuration
public class LogRecordProxyAutoConfiguration implements ImportAware {
    private AnnotationAttributes enableLogRecord;

    /**
     * 实现 ImportAware 接口为了获取 {@link EnableLogRecord} 注解的 tenant 参数
     *
     * @param importMetadata
     */
    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableLogRecord = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableLogRecord.class.getName(), false));
        if (this.enableLogRecord == null) {
            log.info("@EnableLogRecord is not present on importing class");
        }
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordOperationSource logRecordOperationSource() {
        return new LogRecordOperationSource();
    }

    /**
     * 默认 parser 实现
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IParseFunction.class)
    public DefaultParseFunction parseFunction() {
        return new DefaultParseFunction();
    }

    /**
     * @Autowired List<IParseFunction> parseFunctions
     * 在ioc 创建bean 时，注入属性，会把IParseFunction 的所有bean定义信息扫描后，注入子类实例
     *
     * @param parseFunctions
     * @return
     */
    @Bean
    public ParseFunctionFactory parseFunctionFactory(@Autowired List<IParseFunction> parseFunctions) {
        return new ParseFunctionFactory(parseFunctions);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordPointcutAdvisor logRecordAdvisor(IFunctionService functionService) {
        LogRecordPointcutAdvisor advisor = new LogRecordPointcutAdvisor();
        advisor.setAdvice(logRecordInterceptor(functionService));
        advisor.setPointcut(logRecordPointcut());
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordAdvice logRecordInterceptor(IFunctionService functionService) {
        LogRecordAdvice interceptor = new LogRecordAdvice();
        interceptor.setLogRecordOperationSource(logRecordOperationSource());
        interceptor.setFunctionService(functionService);
        return interceptor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordPointcut logRecordPointcut() {
        LogRecordPointcut logRecordPointcut = new LogRecordPointcut();
        logRecordPointcut.setLogRecordOperationSource(logRecordOperationSource());
        return logRecordPointcut;
    }

    /**
     * 默认函数实现
     *
     * @param parseFunctionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IFunctionService.class)
    public IFunctionService functionService(ParseFunctionFactory parseFunctionFactory) {
        return new DefaultFunctionServiceImpl(parseFunctionFactory);
    }


    /**
     * 默认查询 operator 实现
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IOperatorGetService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public IOperatorGetService operatorGetService() {
        return new DefaultOperatorGetServiceImpl();
    }

    /**
     * 默认日志记录存储实现
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ILogRecordService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public ILogRecordService recordService() {
        return new DefaultLogRecordServiceImpl();
    }

}
