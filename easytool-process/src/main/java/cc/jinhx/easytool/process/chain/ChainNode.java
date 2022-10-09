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
    private LongSupplier getTimeout;

    /**
     * 重试次数
     */
    private RetryTimesEnum retryTimes;

    /**
     * 创建链路节点
     *
     * @param node       node
     * @param failHandle failHandle
     * @param getTimeout getTimeout
     * @param retryTimes retryTimes
     * @return ChainNode
     */
    public static ChainNode create(AbstractNode node, FailHandleEnum failHandle, LongSupplier getTimeout, RetryTimesEnum retryTimes) {
        ChainNode chainNode = new ChainNode(node, FailHandleEnum.INTERRUPT, TimeoutEnum::getDefaultTimeout, RetryTimesEnum.ONE);

        if (Objects.nonNull(failHandle)) {
            chainNode.setFailHandle(failHandle);
        }
        if (Objects.nonNull(getTimeout)) {
            chainNode.setGetTimeout(getTimeout);
        }
        if (Objects.nonNull(retryTimes)) {
            chainNode.setRetryTimes(retryTimes);
        }

        return chainNode;
    }


    @AllArgsConstructor
    @Getter
    public enum TimeoutEnum {

        SHORT(50L, "短"),
        SHORTER(100L, "较短"),
        COMMONLY(200L, "一般"),
        LONGER(500L, "较长"),
        LONG(1000L, "长"),
        ;

        private final long code;
        private final String msg;

        private static final Map<Long, TimeoutEnum> MAP;

        static {
            MAP = Arrays.stream(TimeoutEnum.values()).collect(Collectors.toMap(TimeoutEnum::getCode, obj -> obj));
        }

        public static boolean containsCode(long code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(long code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static TimeoutEnum getEnum(long code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

        public static long getDefaultTimeout() {
            return TimeoutEnum.COMMONLY.getCode();
        }

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