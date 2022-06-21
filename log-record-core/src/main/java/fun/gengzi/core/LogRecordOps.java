package fun.gengzi.core;

import lombok.Builder;
import lombok.Data;

/**
 * <h1> </h1>
 *
 * @author Administrator
 * @date 2022/6/21 14:43
 */
@Data
@Builder
public class LogRecordOps {
    // 成功模板
    private String successLogTemplate;
    // 失败模板
    private String failLogTemplate;
    // 操作人id
    private String operatorId;

    private String bizKey;
    // 操作日志绑定的业务对象标识
    private String bizNo;
    // 操作日志的种类
    private String category;
    // 扩展参数，记录操作日志的修改详情
    private String detail;
    // 记录日志的条件
    private String condition;
}
