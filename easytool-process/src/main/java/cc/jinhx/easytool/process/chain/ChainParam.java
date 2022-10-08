package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * 链路参数
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@Data
public class ChainParam<T> {

    /**
     * 线程上下文配置
     */
    private Map<Object, AbstractThreadContextConfig> threadContextInitConfigMap;

    /**
     * 线程上下文配置
     */
    private Set<AbstractThreadContextConfig> threadContextInitConfigSet;

    /**
     * 所有节点状态
     */
    private Map<Class<? extends AbstractNode>, Integer> nodeClassStatusMap;

    /**
     * 所有节点重试次数
     */
    private Map<Class<? extends AbstractNode>, Integer> nodeClassRetryCountMap;

    /**
     * 执行完节点计数器
     */
    private CountDownLatch completedNodeCountDownLatch;

    /**
     * 结果
     */
    private ProcessResult<T> processResult;

    /**
     * 失败异常
     */
    private Exception failException;

    /**
     * 是否是业务失败
     */
    private boolean isBusinessFail;

    /**
     * 是否是超时失败
     */
    private boolean isTimeoutFail;


    @AllArgsConstructor
    @Getter
    public enum NodeStatusEnum {

        NOT_STARTED(1, "未开始"),
        ONGOING(2, "进行中"),
        RETRYING(3, "重试中"),
        COMPLETED(4, "已完成");

        private final int code;
        private final String msg;

        private static final Set<Integer> CAN_RUN_NODE_STATUS_SET;

        private static final Map<Integer, NodeStatusEnum> MAP;

        static {
            MAP = Arrays.stream(NodeStatusEnum.values()).collect(Collectors.toMap(NodeStatusEnum::getCode, obj -> obj));

            CAN_RUN_NODE_STATUS_SET = new HashSet<>();
            CAN_RUN_NODE_STATUS_SET.add(NOT_STARTED.getCode());
            CAN_RUN_NODE_STATUS_SET.add(RETRYING.getCode());
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

        public static NodeStatusEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

        /**
         * 获取可执行状态集合
         *
         * @return Set<Integer>
         */
        public static Set<Integer> getCanRunNodeStatusSet() {
            return CAN_RUN_NODE_STATUS_SET;
        }

    }

}