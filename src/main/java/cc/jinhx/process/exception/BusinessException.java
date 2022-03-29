package cc.jinhx.process.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 业务异常，要返回的信息，比如xxx数据不存在
 *
 * @author jinhx
 * @since 2022-03-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 8431670825597478959L;

    private Integer code;

    private String msg;

}
