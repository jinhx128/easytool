package cc.jinhx.easytool.process.demo.node;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.demo.context.DemoContext;
import cc.jinhx.easytool.process.demo.service.DemoService;
import cc.jinhx.easytool.process.node.AbstractNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * DemoGetDataANode
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class DemoGetDataANode extends AbstractNode<DemoContext> {

    @Autowired
    private DemoService demoService;

    @Override
    public Set<Class<? extends AbstractNode>> getDependsOnNodes() {
        return null;
    }

    @Override
    protected boolean isSkip(ChainContext<DemoContext> chainContext) {
        return false;
    }

    @Override
    protected void execute(ChainContext<DemoContext> chainContext) {
        DemoContext demoContextInfo = chainContext.getContextInfo();
        if ("req".equals(demoContextInfo.getReq())){
            demoContextInfo.setDataA(demoService.getDataA());
        }
    }

    @Override
    public void onUnknowFail(ChainContext<DemoContext> chainContext, Exception e) {

    }

    @Override
    public void onBusinessFail(ChainContext<DemoContext> chainContext, BusinessException e) {

    }

    @Override
    public void onTimeoutFail(ChainContext<DemoContext> chainContext) {

    }

}