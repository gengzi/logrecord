package fun.gengzi.save;

import fun.gengzi.aspect.LogRecord;

public interface ILogRecordService {
    /**
     * 保存 log
     *
     * @param logRecord 日志实体
     */
    void record(LogRecord logRecord);

}