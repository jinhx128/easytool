package cc.jinhx.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 逻辑处理器基础信息
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogicHandlerBaseInfo implements Serializable {

    private static final long serialVersionUID = 4035426155692378372L;

    /**
     * 日志
     */
    private String logStr;

    public void setLogStr(String logStr) {
        if (StringUtils.isEmpty(this.logStr)) {
            this.logStr = logStr;
        } else {
            this.logStr += " " + logStr;
        }
    }

}
