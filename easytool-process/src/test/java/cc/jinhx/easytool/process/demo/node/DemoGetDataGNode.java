package cc.jinhx.easytool.process.demo.node;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.demo.context.DemoContext;
import cc.jinhx.easytool.process.demo.service.DemoService;
import cc.jinhx.easytool.process.node.AbstractNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * DemoGetDataENode
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class DemoGetDataGNode extends AbstractNode<DemoContext> {

    @Autowired
    private DemoService demoService;

    @Override
    public Set<Class<? extends AbstractNode>> getDependsOnNodes() {
        return new HashSet<>(Collections.singletonList(DemoGetDataENode.class));
    }

    @Override
    protected boolean isSkip(ChainContext<DemoContext> chainContext) {
        return false;
    }

    @Override
    protected void execute(ChainContext<DemoContext> chainContext) {
        DemoContext demoContextInfo = chainContext.getContextInfo();
        if ("dataE".equals(demoContextInfo.getDataE())){
            demoContextInfo.setDataG(demoService.getDataG());
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