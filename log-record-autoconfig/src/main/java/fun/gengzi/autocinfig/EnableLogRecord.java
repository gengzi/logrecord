package fun.gengzi.autocinfig;


import fun.gengzi.aspect.LogRecordPointcutAdvisor;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * 用于开关日志记录的功能
 *
 * @author Administrator
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogRecordConfigureSelector.class)
public @interface EnableLogRecord {



    /**
     * Indicate how caching advice should be applied. The default is
     * {@link AdviceMode#PROXY}.
     * 设置代码模式
     *
     * @return 代理方式
     * @see AdviceMode
     */
    AdviceMode mode() default AdviceMode.PROXY;


    /**
     * 指定  {@link LogRecordPointcutAdvisor} 的应用顺序
     * 默认为 {@link Ordered#LOWEST_PRECEDENCE} 以便于运行在其他切入点之后，
     * 以便防止拉长其他注解（事务）的执行过程
     *
     *     * <p>Indicate the ordering of the execution of the operation-log advisor
     *      * when multiple advices are applied at a specific joinpoint.</p>
     *      * <br>
     *      * <b>为什么要为 <b>OperationLogPointcutAdvisor</b> 设定 order 属性呢？</b>
     *      * <p>OperationLog 注解并不局限于 Controller 层面，也可以将其用于 Service 中的业务方法。
     *      * 但无论用于哪一层级，往往需要定制 Advisor 的顺序。比如：当  OperationLog 应用于一个
     *      * Transactional 业务方法上，那就一定要确保 OperationLogPointcutAdvisor 优先级高于
     *      * BeanFactoryTransactionAttributeSourceAdvisor，否则 OperationLogPointcutAdvisor
     *      * 中的切面逻辑（持久化、RPC调用等）会拉长整个事务，这是要避免的。</p>
     *      * <br>
     *      * <p>The default is {@link Ordered#LOWEST_PRECEDENCE}.</p>
     *
     * @return
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}
