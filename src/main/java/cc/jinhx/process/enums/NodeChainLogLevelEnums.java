package cc.jinhx.process.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NodeChainLogLevelEnums
 *
 * @author jinhx
 * @since 2018-08-06
 */
@AllArgsConstructor
@Getter
public enum NodeChainLogLevelEnums {

    NO(1, "不打印"),
    BASE(2, "打印基本信息"),
    BASE_AND_TIME(3, "打印基本信息和耗时"),
    BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS(4, "打印基本信息和耗时和第一个和最后一个节点参数"),
    BASE_AND_TIME_AND_ALL_NODES_PARAMS(5, "打印基本信息和耗时和所有节点参数"),
    ;

    private Integer code;
    private String msg;

    private static final Map<Integer, NodeChainLogLevelEnums> MAP;

    static {
        MAP = Arrays.stream(NodeChainLogLevelEnums.values()).collect(Collectors.toMap(NodeChainLogLevelEnums::getCode, obj -> obj));
    }

    public static Boolean containsCode(Integer code) {
        return MAP.containsKey(code);
    }

    public static String getMsg(Integer code) {
        if (!MAP.containsKey(code)) {
            return null;
        }

        return MAP.get(code).getMsg();
    }

    public static NodeChainLogLevelEnums getEnum(Integer code) {
        if (!MAP.containsKey(code)) {
            return null;
        }

        return MAP.get(code);
    }

    public static List<NodeChainLogLevelEnums> getEnums() {
        return Lists.newArrayList(MAP.values());
    }
}
