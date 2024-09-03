package com.zdouble.domain.strategy.service.rule.tree.impl;

import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("rule_lock")
@Slf4j
public class RuleLockLogicTreeNode implements ILogicTreeNode {
    //用户抽奖次数，从redis中读取
    private Long userRaffleCount = 10L;
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(Long strategyId, String userId, Integer awardId, String ruleValue) {
        log.info("规则过滤—次数锁, strategyId:{}, userId:{}, ruleValue:{}",strategyId, userId, ruleValue);

        long raffleCount = 0L;
        try{
            raffleCount = Long.parseLong(ruleValue);
        }catch (Exception e){
            throw new RuntimeException("规则过滤—次数锁异常 ruleValue: " + ruleValue + "配置错误");
        }
        if(userRaffleCount >= raffleCount){
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
