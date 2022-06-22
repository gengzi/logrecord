package fun.gengzi.core;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * 上面使用了 InheritableThreadLocal，所以在线程池的场景下使用 LogRecordContext 会出现问题，
 * 如果支持线程池可以使用阿里巴巴开源的 TTL 框架。那这里为什么不直接设置一个 ThreadLocal> 对象，而是要设置一个 Stack 结构呢？
 */
public class LogRecordContext {

    /**
     * thread local
     */
    private static final InheritableThreadLocal<Deque<Map<String, Object>>> variableMapStack =
            new InheritableThreadLocal<>();


    /**
     * 获取变量信息
     *
     * @return
     */
    public static Map<String, Object> getVariables() {
        Deque<Map<String, Object>> maps = variableMapStack.get();
        return maps.pop();
    }

    /**
     * 设置一个空位置
     */
    public static void putEmptySpan(){

        Deque<Map<String, Object>> stack = new LinkedList<>();
        variableMapStack.set(stack);
    }


    public static void clear(){
        variableMapStack.remove();
    }

}