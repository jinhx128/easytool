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

    private int code;
    private String msg;

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
        NODE_REPEAT("节点重复"),
        NODE_EMPTY("节点为空"),

        CHAIN_UNREGISTERED("链路未注册"),
        CHAIN_INCOMPLETE("链路不完整");

        private final String msg;

    }

}
