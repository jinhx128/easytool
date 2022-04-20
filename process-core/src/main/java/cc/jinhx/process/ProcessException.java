package cc.jinhx.process;

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

    public ProcessException(MsgEnum msgEnum, Throwable e){
        super(msgEnum.getMsg(), e);
        this.msg = msgEnum.getMsg();
        this.code = ProcessResult.BaseEnum.UNKONW_FAIL.getCode();
    }

    public ProcessException(MsgEnum msgEnum){
        msg = msgEnum.getMsg();
        this.code = ProcessResult.BaseEnum.UNKONW_FAIL.getCode();
    }

    public ProcessException(String msg){
        this.msg = msg;
        this.code = ProcessResult.BaseEnum.UNKONW_FAIL.getCode();
    }

    @Getter
    @AllArgsConstructor
    public enum MsgEnum {

        NODE_UNKNOWN("节点未知异常"),
        NODE_TIMEOUT("节点超时"),
        NODE_UNREGISTERED("节点未注册"),

        NODE_CHAIN_CONTEXT_INFO_NOT_NULL("节点链上下文信息不能为空"),
        NODE_CHAIN_LOG_STR_NOT_NULL("节点链日志不能为空"),
        NODE_CHAIN_CLASS_NOT_NULL("节点链实现类不能为空"),
        NODE_CHAIN_THREAD_POOL_EXECUTOR_NOT_NULL("节点链线程池不能为空"),
        NODE_CHAIN_UNREGISTERED("节点链未注册"),

        LOGIC_HANDLER_BASE_INFO_NOT_NULL("逻辑处理器基础信息不能为空"),
        LOGIC_HANDLER_LOG_STR_NOT_NULL("逻辑处理器日志不能为空")
        ;

        private final String msg;

    }

}
