package fun.gengzi.userinfo;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.Operator;

import java.util.Optional;

public class DefaultOperatorGetServiceImpl implements IOperatorGetService {

    @Override
    public Operator getUser() {
    //UserUtils 是获取用户上下文的方法
//         return Optional.ofNullable(UserUtils.getUser())
//                        .map(a -> new Operator(a.getName(), a.getLogin()))
//                        .orElseThrow(()->new IllegalArgumentException("user is null"));

        return null;
        
    }
}