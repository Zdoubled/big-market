package com.zdouble.domain.activity.service.rule.factory;

import com.zdouble.domain.activity.service.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class DefaultActionChainFactory {

    private final IActionChain actionChain;

    public DefaultActionChainFactory(Map<String, IActionChain> actionChainGroup) {
        actionChain = actionChainGroup.get(ActionChainType.ACTIVITY_BASE_ACTION.getType());
        actionChain.appendNext(actionChainGroup.get(ActionChainType.ACTIVITY_SKU_STOCK_ACTION.getType()));
    }

    public IActionChain openChain(){
        return actionChain;
    }
    @Getter
    @AllArgsConstructor
    public enum ActionChainType{

        ACTIVITY_BASE_ACTION("activity_base_action","活动基础信息、状态过滤"),
        ACTIVITY_SKU_STOCK_ACTION("activity_sku_stock_action","活动sku库存过滤"),
        ;

        private final String type;
        private final String desc;
    }
}
