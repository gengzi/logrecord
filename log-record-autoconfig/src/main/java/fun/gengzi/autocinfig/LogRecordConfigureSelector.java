package fun.gengzi.autocinfig;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * <h1>返回需要导入到 spring 容器中的类 </h1>
 *
 * @author Administrator
 * @date 2022/6/22 11:03
 */
public class LogRecordConfigureSelector implements ImportSelector {


    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{LogRecordProxyAutoConfiguration.class.getName()};
    }
}
