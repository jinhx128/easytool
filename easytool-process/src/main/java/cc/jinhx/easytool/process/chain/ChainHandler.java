package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.SpringUtil;
import lombok.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * 链路执行器
 *
 * @author jinhx
 * @since 2022-03-21
 */
public class ChainHandler {

    /**
     * 执行当前链路，使用默认配置的线程池
     *
     * @param nodeClass    nodeClass
     * @param chainContext chainContext
     * @return ProcessResult
     */
    public static <T> ProcessResult<T> execute(@NonNull Class<? extends AbstractChain> nodeClass, @NonNull ChainContext<T> chainContext) {
        return SpringUtil.getBean(nodeClass).execute(chainContext);
    }

    /**
     * 执行当前链路，使用默认配置的线程池
     *
     * @param nodeClass    nodeClass
     * @param chainContext chainContext
     * @param getResultData getResultData
     * @return ProcessResult
     */
    public static <T, R> ProcessResult<R> execute(@NonNull Class<? extends AbstractChain> nodeClass, @NonNull ChainContext<T> chainContext, Function<T, R> getResultData) {
        return SpringUtil.getBean(nodeClass).execute(chainContext, getResultData);
    }

    /**
     * 执行当前链路，指定线程池，如果为空则使用默认配置的线程池
     *
     * @param nodeClass       nodeClass
     * @param chainContext    chainContext
     * @param executorService executorService
     * @return ProcessResult
     */
    public static <T> ProcessResult<T> execute(@NonNull Class<? extends AbstractChain> nodeClass, @NonNull ChainContext<T> chainContext, @NonNull ExecutorService executorService) {
        return SpringUtil.getBean(nodeClass).execute(chainContext, executorService);
    }

    /**
     * 执行当前链路，指定线程池，如果为空则使用默认配置的线程池
     *
     * @param nodeClass       nodeClass
     * @param chainContext    chainContext
     * @param getResultData    getResultData
     * @param executorService executorService
     * @return ProcessResult
     */
    public static <T, R> ProcessResult<R> execute(@NonNull Class<? extends AbstractChain> nodeClass, @NonNull ChainContext<T> chainContext, Function<T, R> getResultData, @NonNull ExecutorService executorService) {
        return SpringUtil.getBean(nodeClass).execute(chainContext, getResultData, executorService);
    }

}
