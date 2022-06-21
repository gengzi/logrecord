package fun.gengzi.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1> </h1>
 *
 * @author Administrator
 * @date 2022/6/21 14:46
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MethodExecuteResult {
    private boolean success;
    private String errorMsg;
    private Object object;
    public Throwable throwable;

    public MethodExecuteResult(boolean success, Object object, String errorMsg) {
        this.success = success;
        this.errorMsg = errorMsg;
        this.object = object;
    }
}
