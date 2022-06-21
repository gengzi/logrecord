package fun.gengzi.function;

public class DefaultFunctionServiceImpl implements IFunctionService {

    private final ParseFunctionFactory parseFunctionFactory;

    public DefaultFunctionServiceImpl(ParseFunctionFactory parseFunctionFactory) {
        this.parseFunctionFactory = parseFunctionFactory;
    }

    /**
     * 根据传入的函数名称 functionName 找到对应的 IParseFunction
     * 然后把参数传入到 IParseFunction 的 apply 方法上最后返回函数的值
     *
     * @param functionName
     * @param value
     * @return
     */
    @Override
    public String apply(String functionName, String value) {
        IParseFunction function = parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            return value;
        }
        return function.apply(value);
    }

    /**
     * 是否在方法执行前，执行解析自定义函数
     *
     * @param functionName
     * @return
     */
    @Override
    public boolean beforeFunction(String functionName) {
        return parseFunctionFactory.isBeforeFunction(functionName);
    }
}