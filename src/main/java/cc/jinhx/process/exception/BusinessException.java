package cc.jinhx.process.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BaseResult
 *
 * @author jinhx
 * @since 2018-08-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 8431670825597478959L;

    private Object data;

    private Integer code;

    private String msg;

    public BusinessException(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(Object data, Integer code, String msg){
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

}
