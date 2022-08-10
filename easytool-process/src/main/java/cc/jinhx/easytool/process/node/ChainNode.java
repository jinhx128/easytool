package cc.jinhx.easytool.process.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
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
    private FailHandleEnum failHandle = FailHandleEnum.INTERRUPT;

    /**
     * 节点执行超时时间
     */
    private Long timeout = TimeoutEnum.COMMONLY.getCode();

    /**
     * 重试次数
     */
    private RetryTimesEnum retryTimes = RetryTimesEnum.ONE;


    @AllArgsConstructor
    @Getter
    public enum TimeoutEnum {

        SHORT(50L, "短"),
        SHORTER(100L, "较短"),
        COMMONLY(200L, "一般"),
        LONGER(500L, "较长"),
        LONG(1000L, "长"),
        ;

        private final Long code;
        private final String msg;

        private static final Map<Long, TimeoutEnum> MAP;

        static {
            MAP = Arrays.stream(TimeoutEnum.values()).collect(Collectors.toMap(TimeoutEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(Long code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(Long code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static TimeoutEnum getEnum(Long code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

    @AllArgsConstructor
    @Getter
    public enum LogLevelEnum {

        NO(1, "不打印"),
        BASE(2, "打印基本信息"),
        BASE_AND_TIME(3, "打印基本信息和耗时"),
        BASE_AND_TIME_AND_PARAMS(4, "打印基本信息和耗时和参数"),
        ;

        private final int code;
        private final String msg;

        private static final Map<Integer, LogLevelEnum> MAP;

        static {
            MAP = Arrays.stream(LogLevelEnum.values()).collect(Collectors.toMap(LogLevelEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(int code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static LogLevelEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

    @AllArgsConstructor
    @Getter
    public enum FailHandleEnum {

        INTERRUPT(1, "中断链路"),
        ABANDON(2, "抛弃节点"),
        RETRY(3, "重试节点"),
        ;

        private final int code;
        private final String msg;

        private static final Map<Integer, FailHandleEnum> MAP;

        static {
            MAP = Arrays.stream(FailHandleEnum.values()).collect(Collectors.toMap(FailHandleEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(int code) {
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

        public static Boolean containsCode(int code) {
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
