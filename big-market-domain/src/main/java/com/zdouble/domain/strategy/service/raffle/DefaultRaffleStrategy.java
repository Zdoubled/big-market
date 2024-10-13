package com.zdouble.domain.strategy.service.raffle;

import com.zdouble.domain.strategy.IRaffleRule;
import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.model.vo.RuleTreeVO;
import com.zdouble.domain.strategy.model.vo.RuleWeightVO;
import com.zdouble.domain.strategy.model.vo.StrategyAwardKeyStockVO;
import com.zdouble.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.AbstractRaffleStrategy;
import com.zdouble.domain.strategy.service.IRaffleAward;
import com.zdouble.domain.strategy.service.IRaffleStock;
import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import com.zdouble.domain.strategy.service.rule.chain.ILogicChain;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.zdouble.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleStock, IRaffleAward, IRaffleRule {

    public DefaultRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory, DefaultTreeFactory defaultTreeFactory) {
        super(strategyRepository, strategyDispatch, defaultLogicChainFactory, defaultTreeFactory);
    }

    @Override
    protected DefaultLogicChainFactory.StrategyAwardVO raffleLogicChain(Long strategyId, String userId) {
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(strategyId);
        return logicChain.logic(strategyId, userId);
    }

    @Override
    protected DefaultTreeFactory.StrategyAwardVO raffleLogicTree(Long strategyId, String userId, Integer awardId) {
        return raffleLogicTree(strategyId, userId, awardId, null);
    }

    @Override
    protected DefaultTreeFactory.StrategyAwardVO raffleLogicTree(Long strategyId, String userId, Integer awardId, Date endTime) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        if (strategyAwardRuleModelVO == null) {
            return DefaultTreeFactory.StrategyAwardVO.builder().awardId(awardId).build();
        }
        //1.查询并装配规则树
        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if (null == ruleTreeVO) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + strategyAwardRuleModelVO.getRuleModels());
        }
        IDecisionTreeEngine decisionTreeEngine = defaultTreeFactory.openTreeEngine(ruleTreeVO);
        return decisionTreeEngine.process(strategyId, userId, awardId, endTime);
    }

    @Override
    public StrategyAwardKeyStockVO takeQueueValue() throws InterruptedException {
        return strategyRepository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        strategyRepository.updateStrategyAwardStock(strategyId, awardId);
    }

    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long activityId) {
        // 根据活动id查询策略id
        Long strategyId = strategyRepository.queryStrategyIdByActivityId(activityId);
        // 根据策略id查询奖品信息
        return strategyRepository.queryStrategyAwardList(strategyId);
    }

    @Override
    public HashMap<String, Integer> queryRuleLockCount(String[] treeIds) {
        if (null == treeIds || treeIds.length == 0) return new HashMap<String,Integer>();
        return strategyRepository.queryRuleLockCount(treeIds);
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeightByArticleId(Long articleId) {
        // 1. 查询活动配置奖品信息
        Long strategyId = strategyRepository.queryStrategyIdByActivityId(articleId);
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.queryStrategyAwardList(strategyId);
        // 2. 查询活动权重策略配置
        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, "rule_weight");
        String[] split = ruleValue.split(" ");
        if(split.length == 0) return null;
        ArrayList<RuleWeightVO> ruleWeightVOS = new ArrayList<>();
        for (String ruleWeight : split) {
            String[] ruleWeightSplit = ruleWeight.split(":");
            String[] awardIds = ruleWeightSplit[1].split(",");
            List<RuleWeightVO.Award> awards = strategyAwardEntities.stream().filter(strategyAwardEntity -> {
                for (String awardId : awardIds) {
                    if (Integer.valueOf(awardId).equals(strategyAwardEntity.getAwardId())) {
                        return true;
                    }
                }
                return false;
            }).map(strategyAwardEntity -> {
                return RuleWeightVO.Award.builder()
                        .awardId(strategyAwardEntity.getAwardId())
                        .awardTitle(strategyAwardEntity.getAwardTitle())
                        .build();
            }).collect(Collectors.toList());

            RuleWeightVO ruleWeightVO = RuleWeightVO.builder()
                    .wight(Integer.valueOf(ruleWeightSplit[0]))
                    .awardList(awards)
                    .build();
            ruleWeightVOS.add(ruleWeightVO);
        }
        return ruleWeightVOS;
    }
}
