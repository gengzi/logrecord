package fun.gengzi.core;

import java.util.*;

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
     * 保存变量信息到 stack 中
     *
     * @param name
     * @param value
     */
    public static void putVariable(String name, Object value) {
        if (variableMapStack.get() == null) {
            Deque<Map<String, Object>> stack = new LinkedList<>();
            variableMapStack.set(stack);
        }
        Deque<Map<String, Object>> mapStack = variableMapStack.get();
        if (mapStack.size() == 0) {
            variableMapStack.get().push(new HashMap<>());
        }
        variableMapStack.get().peek().put(name, value);
    }

    public static Object getVariable(String key) {
        Map<String, Object> variableMap = variableMapStack.get().peek();
        return variableMap.get(key);

    }


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
     * 栈未初始化，先初始化栈
     */
    public static void putEmptySpan() {
        Deque<Map<String, Object>> stack = variableMapStack.get();
        if (stack == null) {
            // 新建一个
            stack = new LinkedList<>();
            variableMapStack.set(stack);
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        stack.push(paramMap);
    }


    /**
     * 清除资源
     */
    public static void clear() {
        if (variableMapStack.get() != null) {
            variableMapStack.get().pop();

        }
    }

}