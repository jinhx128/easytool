package cc.jinhx.easytool.process.chain;

import lombok.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 单个类型线程上下文配置
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SingletonThreadContextConfig<T> extends AbstractThreadContextConfig {

    @NonNull
    private Supplier<T> getContext;

    @NonNull
    private Consumer<T> setContext;

    @NonNull
    private Runnable removeContext;

}