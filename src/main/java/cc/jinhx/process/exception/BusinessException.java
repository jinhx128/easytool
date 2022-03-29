package cc.jinhx.process.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * BusinessException
 *
 * @author jinhx
 * @since 2018-08-06
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
