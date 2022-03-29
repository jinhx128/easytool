package cc.jinhx.process.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NodeTimeoutEnums
 *
 * @author jinhx
 * @since 2022-03-21
 */
@AllArgsConstructor
@Getter
public enum NodeTimeoutEnums {

    SHORT(50L, "短"),
    SHORTER(100L, "较短"),
    COMMONLY(200L, "一般"),
    LONGER(500L, "较长"),
    LONG(1000L, "长"),
    ;

    private Long code;
    private String msg;

    private static final Map<Long, NodeTimeoutEnums> MAP;

    static {
        MAP = Arrays.stream(NodeTimeoutEnums.values()).collect(Collectors.toMap(NodeTimeoutEnums::getCode, obj -> obj));
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

    public static NodeTimeoutEnums getEnum(Long code) {
        if (!MAP.containsKey(code)) {
            return null;
        }

        return MAP.get(code);
    }

}