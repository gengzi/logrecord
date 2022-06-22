package fun.gengzi.aspect;

import java.lang.annotation.*;

/**
 * <h1> 业务日志注解 </h1>
 *
 *  用于标记方法上的业务操作说明，提供给用户查看
 *
 *
 *  对 spel 的说明
 *  SpEL 还通过使用标准点表示法（例如 prop1.prop2.prop3）以及相应的属性值设置来支持嵌套属性。也可以访问公共字段。
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * @author Administrator
 * @date 2022/6/21 10:53
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
// 当以后我们在定义一个作用于类的注解时候，如果希望该注解也作用于其子类，那么可以用@Inherited 来进行修饰
@Inherited
@Documented
public @interface LogRecord {
    /**
     * 操作日志的文本模板
     *
     * @return
     */
    String success();

    /**
     * 操作日志失败的文本版本
     *
     * @return
     */
    String fail() default "";

    /**
     * 操作日志的执行人
     *
     * @return
     */
    String operator() default "";

    /**
     * 操作日志绑定的业务对象标识
     *
     * @return
     */
    String bizNo();

    /**
     * 操作日志的种类
     *
     * @return
     */
    String category() default "";

    /**
     * 扩展参数，记录操作日志的修改详情
     *
     * @return
     */
    String detail() default "";

    /**
     * 记录日志的条件
     *
     * @return
     */
    String condition() default "";


    /**
     * 前缀标记
     * @return
     */
    String prefix() default "";


}
