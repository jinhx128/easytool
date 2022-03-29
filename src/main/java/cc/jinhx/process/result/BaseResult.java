package cc.jinhx.process.result;

import cc.jinhx.process.enums.ResultEnums;
import lombok.Data;

import java.io.Serializable;

/**
 * BaseResult
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = 8431670825594478958L;

    private T data;

    private Integer code;

    private String msg;

    private BaseResult(){
    }

    public BaseResult(T data){
        this.data = data;
        this.code = ResultEnums.SUCCESS.getCode();
        this.msg = ResultEnums.SUCCESS.getMsg();
    }

    public BaseResult(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess(){
        return ResultEnums.SUCCESS.getCode().equals(this.code);
    }

    public boolean isFail(){
        return !isSuccess();
    }

}
