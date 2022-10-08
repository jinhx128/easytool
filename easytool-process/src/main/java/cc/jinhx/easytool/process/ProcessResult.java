package cc.jinhx.easytool.process;

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

    private int code;

    private String msg;

    private ProcessResult() {
    }

    public ProcessResult(T data) {
        this.data = data;
        this.code = BaseEnum.SUCCESS.getCode();
        this.msg = BaseEnum.SUCCESS.getMsg();
    }

    public ProcessResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return BaseEnum.SUCCESS.getCode() == this.code;
    }

    public boolean isBusinessFail() {
        return BaseEnum.BUSINESS_FAIL.getCode() == this.code;
    }

    public boolean isUnknowFail() {
        return BaseEnum.UNKNOW_FAIL.getCode() == this.code;
    }

    public boolean isTimeoutFail() {
        return BaseEnum.TIMEOUT_FAIL.getCode() == this.code;
    }


    @AllArgsConstructor
    @Getter
    public enum BaseEnum {

        SUCCESS(1, "success"),
        TIMEOUT_FAIL(2, "timeout fail"),
        BUSINESS_FAIL(3, "business fail"),
        UNKNOW_FAIL(4, "unknown fail");

        private final int code;
        private final String msg;

        private static final Map<Integer, BaseEnum> MAP;

        static {
            MAP = Arrays.stream(BaseEnum.values()).collect(Collectors.toMap(BaseEnum::getCode, obj -> obj));
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

        public static BaseEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

}