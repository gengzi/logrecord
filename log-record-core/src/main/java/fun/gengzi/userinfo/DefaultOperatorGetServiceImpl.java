package fun.gengzi.userinfo;


import fun.gengzi.core.Operator;

public class DefaultOperatorGetServiceImpl implements IOperatorGetService {

    @Override
    public Operator getUser() {
        //UserUtils 是获取用户上下文的方法
//         return Optional.ofNullable(UserUtils.getUser())
//                        .map(a -> new Operator(a.getName(), a.getLogin()))
//                        .orElseThrow(()->new IllegalArgumentException("user is null"));

        return new Operator("test");
    }
}