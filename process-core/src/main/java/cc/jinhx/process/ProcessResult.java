package cc.jinhx.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ProcessResult
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
public class ProcessResult<T> implements Serializable {

    private static final long serialVersionUID = 8431670825594478958L;

    private T data;

    private Integer code;

    private String msg;

    private ProcessResult() {
    }

    public ProcessResult(T data) {
        this.data = data;
        this.code = BaseEnum.SUCCESS.getCode();
        this.msg = BaseEnum.SUCCESS.getMsg();
    }

    public ProcessResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return BaseEnum.SUCCESS.getCode().equals(this.code);
    }

    public boolean isBusinessFail() {
        return BaseEnum.BUSINESS_FAIL.getCode().equals(this.code);
    }

    public boolean isUnknowFail() {
        return BaseEnum.UNKNOW_FAIL.getCode().equals(this.code);
    }


    @AllArgsConstructor
    @Getter
    public enum BaseEnum {

        SUCCESS(1, "success"),
        BUSINESS_FAIL(0, "business fail"),
        UNKNOW_FAIL(-1, "unknown fail");

        private final Integer code;
        private final String msg;

        private static final Map<Integer, BaseEnum> MAP;

        static {
            MAP = Arrays.stream(BaseEnum.values()).collect(Collectors.toMap(BaseEnum::getCode, obj -> obj));
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

        public static BaseEnum getEnum(Integer code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

}
