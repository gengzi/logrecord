package fun.gengzi.save;

import fun.gengzi.aspect.LogRecord;
import fun.gengzi.core.LogRecordOps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLogRecordServiceImpl implements ILogRecordService {

    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(LogRecordOps logRecord) {
        log.info("【logRecord】log={}", logRecord);
    }
}