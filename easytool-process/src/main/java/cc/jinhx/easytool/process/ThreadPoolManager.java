package cc.jinhx.easytool.process;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPoolManager
 *
 * @author jinhx
 * @since 2022-04-14
 */
@Slf4j
public class ThreadPoolManager {

    private static final AtomicInteger COMMON_CHAIN_THREAD_POOL_COUNTER = new AtomicInteger(0);

    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 公共异步链路线程池
     */
    public static final ThreadPoolExecutor COMMON_CHAIN_THREAD_POOL = new ThreadPoolExecutor(
            2, CPU_NUM * 2,
            10, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(1024),
            (Runnable r) -> new Thread(r, "asyncCommonChain_thread_" + COMMON_CHAIN_THREAD_POOL_COUNTER.incrementAndGet()),
            (r, executor) -> log.info("process async common chain has bean rejected" + r));

}

