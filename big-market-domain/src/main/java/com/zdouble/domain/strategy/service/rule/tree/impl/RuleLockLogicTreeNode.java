package com.zdouble.domain.strategy.service.rule.tree.impl;

import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("rule_lock")
@Slf4j
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(Long strategyId, String userId, Integer awardId, String ruleValue) {
        log.info("规则过滤—次数锁, strategyId:{}, userId:{}, ruleValue:{}",strategyId, userId, ruleValue);

        long raffleCount = 0L;
        try{
            raffleCount = Long.parseLong(ruleValue);
        }catch (Exception e){
            throw new RuntimeException("规则过滤—次数锁异常 ruleValue: " + ruleValue + "配置错误");
        }
        int userRaffleCount = strategyRepository.queryTodayUserRaffleCount(userId, strategyId);
        // 次数满足，解锁放行
        if(userRaffleCount >= raffleCount){
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }
        // 次数不满足，拦截
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
