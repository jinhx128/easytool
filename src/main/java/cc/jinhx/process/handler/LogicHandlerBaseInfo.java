package cc.jinhx.process.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * 逻辑处理器基础信息
 *
 * @author jinhx
 * @since 2021-08-06
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

    public void setLogStr(String logStr){
        if (StringUtils.isBlank(this.logStr)){
            this.logStr = logStr;
        }else {
            this.logStr += " " + logStr;
        }
    }

}
