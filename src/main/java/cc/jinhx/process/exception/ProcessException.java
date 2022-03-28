package cc.jinhx.process.exception;

import cc.jinhx.process.enums.ExceptionEnums;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ProcessException
 *
 * @author jinhx
 * @since 2018-08-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProcessException extends RuntimeException {

    private static final long serialVersionUID = 8431670825597478958L;

    private String msg;

    public ProcessException(ExceptionEnums responseEnums, Throwable e){
        super(responseEnums.getMsg(), e);
        msg = responseEnums.getMsg() + "=" + responseEnums.getMsg();
    }

    public ProcessException(ExceptionEnums responseEnums){
        msg = responseEnums.getMsg();
    }

    public ProcessException(String msg){
        this.msg = msg;
    }

}
