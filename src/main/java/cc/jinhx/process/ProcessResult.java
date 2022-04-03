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

    private ProcessResult(){
    }

    public ProcessResult(T data){
        this.data = data;
        this.code = ProcessResult.BaseEnum.SUCCESS.getCode();
        this.msg = ProcessResult.BaseEnum.SUCCESS.getMsg();
    }

    public ProcessResult(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess(){
        return ProcessResult.BaseEnum.SUCCESS.getCode().equals(this.code);
    }

    public boolean isFail(){
        return !isSuccess();
    }


    @AllArgsConstructor
    @Getter
    public enum BaseEnum {

        SUCCESS(1, "success"),
        FAIL(-1, "fail")
        ;

        private Integer code;
        private String msg;

        private static final Map<Integer, ProcessResult.BaseEnum> MAP;

        static {
            MAP = Arrays.stream(ProcessResult.BaseEnum.values()).collect(Collectors.toMap(ProcessResult.BaseEnum::getCode, obj -> obj));
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

        public static ProcessResult.BaseEnum getEnum(Integer code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

}
