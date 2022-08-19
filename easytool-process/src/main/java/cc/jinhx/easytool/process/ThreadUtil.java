package cc.jinhx.easytool.process;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * ThreadUtil
 *
 * @author jinhx
 * @since 2022-04-14
 */
@Slf4j
public class ThreadUtil {

    private static final AtomicInteger COMMON_CHAIN_THREAD_POOL_COUNTER = new AtomicInteger(0);

    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 公共链路线程池
     */
    public static final ThreadPoolExecutor COMMON_CHAIN_THREAD_POOL =
            new ThreadPoolExecutor(
                    2, CPU_NUM * 2,
                    10, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(1024),
                    (Runnable r) -> new Thread(r, "asyncCommonChain_thread_" + COMMON_CHAIN_THREAD_POOL_COUNTER.incrementAndGet()),
                    (r, executor) -> log.info("process async common chain has bean rejected" + r));

    /**
     * 链路监控线程池
     */
    public static final ScheduledExecutorService CHAIN_MONITOR_SCHEDULER =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread thread = new Thread(r);
                thread.setName("asyncChainMonitor_thread");
                thread.setDaemon(true);
                return thread;
            });

    /**
     * 定时失败线程池
     */
    private static final ScheduledExecutorService TIMING_FAIL_SCHEDULER =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread thread = new Thread(r);
                thread.setName("asyncTimingFail_thread");
                thread.setDaemon(true);
                return thread;
            });

    /**
     * 获取定时失败future
     *
     * @param duration duration
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> getTimingFailFuture(Duration duration) {
        CompletableFuture<T> future = new CompletableFuture<>();
        TIMING_FAIL_SCHEDULER.schedule(() -> future.completeExceptionally(new TimeoutException("timeout after " + duration.toMillis())), duration.toMillis(), TimeUnit.MILLISECONDS);
        return future;
    }

    /**
     * 判断是否在指定时间内完成
     *
     * @param future   future
     * @param duration duration
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> withinTime(CompletableFuture<T> future, Duration duration) {
        return future.applyToEither(getTimingFailFuture(duration), Function.identity());
    }

}