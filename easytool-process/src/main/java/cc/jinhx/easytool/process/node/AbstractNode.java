package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.chain.ChainContext;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 抽象节点
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@Slf4j
public abstract class AbstractNode<T> {

    private final String LOG_PREFIX = "process nodeLog ";
    private final String LOG_END = " execute success";
    private final String CHAIN = " chain ";
    private final String NODE = " node ";
    private final String LOG_SKIP = " skip=";
    private final String LOG_TIME = " time=";
    private final String TRUE = "true";
    private final String FALSE = "false";

    /**
     * 获取依赖节点集合
     *
     * @return 依赖节点集合
     */
    public abstract Set<Class<? extends AbstractNode>> getDependsOnNodes();

    /**
     * 是否跳过当前节点
     *
     * @param chainContext chainContext
     * @return 是否跳过当前执行方法
     */
    protected abstract boolean isSkip(ChainContext<T> chainContext);

    /**
     * 节点执行方法
     *
     * @param chainContext chainContext
     */
    protected abstract void execute(ChainContext<T> chainContext);

    /**
     * 通用执行方法
     *
     * @param chainContext chainContext
     * @param chainName    chainName
     */
    public void doExecute(@NonNull ChainContext<T> chainContext, String chainName) {
        StringBuilder logInfo = new StringBuilder();
        logInfo.append(LOG_PREFIX).append(chainContext.getLogStr()).append(LOG_END).append(CHAIN).append("[").append(chainName)
                .append("]").append(NODE).append("[").append(this.getClass().getName()).append("]");

        long startTime = System.currentTimeMillis();

        if (isSkip(chainContext)) {
            logInfo.append(LOG_SKIP).append(TRUE);
        } else {
            logInfo.append(LOG_SKIP).append(FALSE);
            execute(chainContext);
        }

        logInfo.append(LOG_TIME).append(System.currentTimeMillis() - startTime);
        log.info(logInfo.toString());
        // 手动释放内存
        logInfo.setLength(0);
    }


    /**
     * 业务失败
     *
     * @param code code
     * @param msg  msg
     */
    protected void businessFail(int code, String msg) {
        throw new BusinessException(code, msg);
    }

    /**
     * 业务失败
     *
     * @param msg msg
     */
    protected void businessFail(String msg) {
        throw new BusinessException(ProcessResult.BaseEnum.BUSINESS_FAIL.getCode(), msg);
    }


    /**
     * 成功时执行
     *
     * @param chainContext chainContext
     */
    public void onSuccess(@NonNull ChainContext<T> chainContext) {
    }

    /**
     * 超时失败时执行
     *
     * @param chainContext chainContext
     */
    public abstract void onTimeoutFail(@NonNull ChainContext<T> chainContext);

    /**
     * 业务失败时执行
     *
     * @param chainContext chainContext
     */
    public abstract void onBusinessFail(@NonNull ChainContext<T> chainContext, @NonNull BusinessException e);

    /**
     * 未知失败时执行
     *
     * @param chainContext chainContext
     */
    public abstract void onUnknowFail(@NonNull ChainContext<T> chainContext, @NonNull Exception e);

    /**
     * 无论成功失败，最后都会执行
     */
    public void afterExecute(@NonNull ChainContext<T> chainContext) {
    }

}
