package fun.gengzi.aspect;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * <h1>spring 切面通知</h1>
 *
 * @author Administrator
 * @date 2022/6/21 13:47
 */
public class LogRecordPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private Pointcut pointcut;

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    public LogRecordPointcutAdvisor() {
    }

    /**
     * 初始化pointcut  和 advice
     *
     * @param pointcut
     * @param advice
     */
    public LogRecordPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        setAdvice(advice);
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }
}
