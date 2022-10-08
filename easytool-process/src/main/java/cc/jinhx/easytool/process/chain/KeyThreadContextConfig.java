package cc.jinhx.easytool.process.chain;

import lombok.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * key类型线程上下文配置
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KeyThreadContextConfig<T, R> extends AbstractThreadContextConfig {

    @NonNull
    private T key;

    @NonNull
    private Function<T, R> getContextByKey;

    @NonNull
    private BiConsumer<T, R> setContextByKey;

    @NonNull
    private Consumer<T> removeContextByKey;

}