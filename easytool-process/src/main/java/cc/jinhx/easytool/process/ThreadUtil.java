package cc.jinhx.easytool.process;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * ThreadUtil
 *
 * @author jinhx
 * @since 2022-04-14
 */
@Slf4j
public class ThreadUtil {

    /**
     * 用于计时的线程池
     */
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1, r -> {
                Thread thread = new Thread(r);
                thread.setName("failAfter-%d");
                thread.setDaemon(true);
                return thread;
            });

    /**
     * 用于计时的future
     *
     * @param duration duration
     * @return CompletableFuture<T>
     */
    public static <T> CompletableFuture<T> failAfter(Duration duration) {
        CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> promise.completeExceptionally(new TimeoutException("Timeout after " + duration.toMillis())), duration.toMillis(), TimeUnit.MILLISECONDS);
        return promise;
    }

    /**
     * 用于判断是否超时
     *
     * @param future   future
     * @param duration duration
     */
    public static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        return future.applyToEither(failAfter(duration), Function.identity());
    }

}

