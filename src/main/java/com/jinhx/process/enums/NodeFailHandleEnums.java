package com.jinhx.process.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NodeFailHandleEnums
 *
 * @author jinhx
 * @since 2018-08-06
 */
@AllArgsConstructor
@Getter
public enum NodeFailHandleEnums {

    INTERRUPT(1, "中断链路"),
    ABANDON(2, "抛弃节点"),
//    RETRY(3, "重试节点"),
    ;

    private Integer code;
    private String msg;

    private static final Map<Integer, NodeFailHandleEnums> MAP;

    static {
        MAP = Arrays.stream(NodeFailHandleEnums.values()).collect(Collectors.toMap(NodeFailHandleEnums::getCode, obj -> obj));
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

    public static NodeFailHandleEnums getEnum(Integer code) {
        if (!MAP.containsKey(code)) {
            return null;
        }

        return MAP.get(code);
    }

    public static List<NodeFailHandleEnums> getEnums() {
        return Lists.newArrayList(MAP.values());
    }

}