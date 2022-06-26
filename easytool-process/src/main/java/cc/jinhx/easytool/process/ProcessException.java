package cc.jinhx.easytool.process;

import lombok.*;

/**
 * ProcessException
 *
 * @author jinhx
 * @since 2022-03-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessException extends RuntimeException {

    private static final long serialVersionUID = 8431670825597478958L;

    private String msg;
    private Integer code;

    public ProcessException(MsgEnum msgEnum, Throwable e) {
        super(msgEnum.getMsg(), e);
        this.msg = msgEnum.getMsg();
        this.code = ProcessResult.BaseEnum.UNKNOW_FAIL.getCode();
    }

    public ProcessException(MsgEnum msgEnum) {
        msg = msgEnum.getMsg();
        this.code = ProcessResult.BaseEnum.UNKNOW_FAIL.getCode();
    }

    public ProcessException(String msg) {
        this.msg = msg;
        this.code = ProcessResult.BaseEnum.UNKNOW_FAIL.getCode();
    }

    @Getter
    @AllArgsConstructor
    public enum MsgEnum {

        NODE_UNKNOWN("节点未知异常"),
        NODE_TIMEOUT("节点超时"),
        NODE_UNREGISTERED("节点未注册"),

        TOPOLOGY_UNREGISTERED("拓扑图未注册");

        private final String msg;

    }

}
