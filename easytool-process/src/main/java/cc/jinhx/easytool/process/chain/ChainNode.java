package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

/**
 * 链路节点
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChainNode {

    /**
     * 默认节点超时时间，单位毫秒
     */
    private static final long DEFAULT_NODE_TIMEOUT = 200L;

    /**
     * 节点对象
     */
    private AbstractNode node;

    /**
     * 节点失败处理
     */
    private FailHandleEnum failHandle;

    /**
     * 获取节点执行超时时间
     */
    private LongSupplier getNodeTimeout;

    /**
     * 重试次数
     */
    private RetryTimesEnum retryTimes;


    /**
     * 获取默认节点超时时间，单位毫秒
     *
     * @return 默认节点超时时间，单位毫秒
     */
    public static long getDefaultNodeTimeout() {
        return DEFAULT_NODE_TIMEOUT;
    }

    /**
     * 创建链路节点
     *
     * @param node           node
     * @param failHandle     failHandle
     * @param getNodeTimeout getNodeTimeout
     * @param retryTimes     retryTimes
     * @return ChainNode
     */
    public static ChainNode create(AbstractNode node, FailHandleEnum failHandle, LongSupplier getNodeTimeout, RetryTimesEnum retryTimes) {
        ChainNode chainNode = new ChainNode(node, FailHandleEnum.INTERRUPT, ChainNode::getDefaultNodeTimeout, RetryTimesEnum.ONE);

        if (Objects.nonNull(failHandle)) {
            chainNode.setFailHandle(failHandle);
        }
        if (Objects.nonNull(getNodeTimeout)) {
            chainNode.setGetNodeTimeout(getNodeTimeout);
        }
        if (Objects.nonNull(retryTimes)) {
            chainNode.setRetryTimes(retryTimes);
        }

        return chainNode;
    }


    @AllArgsConstructor
    @Getter
    public enum FailHandleEnum {

        INTERRUPT(1, "中断链路", new InterruptFailHandle()),
        ABANDON(2, "抛弃节点", new AbandonFailHandle()),
        RETRY(3, "重试节点", new RetryFailHandle()),
        ;

        private final int code;
        private final String msg;
        private final AbstractFailHandle failHandle;

        private static final Map<Integer, FailHandleEnum> MAP;

        static {
            MAP = Arrays.stream(FailHandleEnum.values()).collect(Collectors.toMap(FailHandleEnum::getCode, obj -> obj));
        }

        public static boolean containsCode(int code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static FailHandleEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }


    @AllArgsConstructor
    @Getter
    public enum RetryTimesEnum {

        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        TEN(10);

        private final int code;

        private static final Map<Integer, RetryTimesEnum> MAP;

        static {
            MAP = Arrays.stream(RetryTimesEnum.values()).collect(Collectors.toMap(RetryTimesEnum::getCode, obj -> obj));
        }

        public static boolean containsCode(int code) {
            return MAP.containsKey(code);
        }

        public static RetryTimesEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

}