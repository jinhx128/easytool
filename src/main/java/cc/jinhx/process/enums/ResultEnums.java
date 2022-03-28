package cc.jinhx.process.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ResultEnums
 *
 * @author jinhx
 * @since 2018-08-06
 */
@AllArgsConstructor
@Getter
public enum ResultEnums {

    SUCCESS(1, "成功"),
    FAIL(-1, "失败")
    ;

    private Integer code;
    private String msg;

    private static final Map<Integer, ResultEnums> MAP;

    static {
        MAP = Arrays.stream(ResultEnums.values()).collect(Collectors.toMap(ResultEnums::getCode, obj -> obj));
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

    public static ResultEnums getEnum(Integer code) {
        if (!MAP.containsKey(code)) {
            return null;
        }

        return MAP.get(code);
    }

    public static List<ResultEnums> getEnums() {
        return Lists.newArrayList(MAP.values());
    }

}