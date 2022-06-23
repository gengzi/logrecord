package fun.gengzi.save;

import fun.gengzi.aspect.LogRecord;
import fun.gengzi.core.LogRecordOps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLogRecordServiceImpl implements ILogRecordService {


    /**
     * 数据库保存，需要新建事务，不要拉长目标方法的事务，与目标方法的事务隔离
     * @param logRecord 日志实体
     */
    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(LogRecordOps logRecord) {
        log.info("【logRecord】log={}", logRecord);
    }
}