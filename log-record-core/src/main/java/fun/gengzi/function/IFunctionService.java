package fun.gengzi.function;

public interface IFunctionService {

    String apply(String functionName, String value);


    public boolean beforeFunction(String functionName);


}
