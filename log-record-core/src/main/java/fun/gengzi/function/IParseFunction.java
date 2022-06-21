package fun.gengzi.function;

/**
 * 自定义函数
 *
 */
public interface IParseFunction {

  /**
   * executeBefore 函数代表了自定义函数是否在业务代码执行之前解析
   * @return
   */
  default boolean executeBefore(){
    return false;
  }

  /**
   * 方法名称
   * @return
   */
  String functionName();

  /**
   *
   * @param value
   * @return
   */
  String apply(String value);
}