package cc.jinhx.process.result;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * BaseResult
 *
 * @author jinhx
 * @since 2018-08-06
 */
@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = 8431670825594478958L;

    private T data;

    private Integer code;

    private String msg;

    public BaseResult(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public BaseResult(T data){
        this.data = data;
    }

    public boolean isSuccess(){
        return Objects.isNull(code) && Objects.isNull(msg);
    }

    public boolean isFail(){
        return !isSuccess();
    }

}
