package com.zdouble.infrastructure.persistent.repository;

import com.zdouble.domain.activity.model.entity.ActivityAccountDayEntity;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.model.entity.StrategyEntity;
import com.zdouble.domain.strategy.model.entity.StrategyRuleEntity;
import com.zdouble.domain.strategy.model.vo.*;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.infrastructure.persistent.dao.*;
import com.zdouble.infrastructure.persistent.po.*;
import com.zdouble.infrastructure.persistent.redis.IRedisService;
import com.zdouble.types.common.Constants;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zdouble.types.enums.ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY;

@Repository
@Slf4j
public class StrategyRepository implements IStrategyRepository {
    @Resource
    private IRedisService redisService;
    @Resource
    private StrategyAwardDao strategyAwardDao;
    @Resource
    private StrategyDao strategyDao;
    @Resource
    private StrategyRuleDao strategyRuleDao;
    @Resource
    private RuleTreeDao ruleTreeDao;
    @Resource
    private RuleTreeNodeDao ruleTreeNodeDao;
    @Resource
    private RuleTreeNodeLineDao ruleTreeNodeLineDao;
    @Resource
    private RaffleActivityDao raffleActivityDao;
    @Resource
    private RaffleActivityAccountDao raffleActivityAccountDao;
    @Resource
    private RaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        //1.查询redis缓存
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntryList = redisService.getValue(cacheKey);
        if (null != strategyAwardEntryList && !strategyAwardEntryList.isEmpty()) {
            return strategyAwardEntryList;
        }
        //2.从库中查询策略配置,并缓存到redis中
        List<StrategyAward> strategyAwardList = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntryList = strategyAwardList.stream().map(strategyAward -> {
            return StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardTitle(strategyAward.getAwardTitle())
                    .awardSubTitle(strategyAward.getAwardSubTitle())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .ruleModels(strategyAward.getRuleModels())
                    .sort(strategyAward.getSort())
                    .build();
        }).collect(Collectors.toList());
        redisService.setValue(cacheKey, strategyAwardEntryList);
        return strategyAwardEntryList;
    }

    @Override
    public void setStrategyAwardSearchRateTable(String key, int rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        //1.缓存策略的概率范围
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);
        //2.缓存策略的抽奖概率范围表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String key) {
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key;
        Integer rateRange = redisService.getValue(cacheKey);
        if (null == rateRange) {
            throw new AppException(UN_ASSEMBLED_STRATEGY_ARMORY.getCode(), UN_ASSEMBLED_STRATEGY_ARMORY.getInfo());
        }
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int rateKey) {
        return (Integer) redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key).get(rateKey);
    }

    @Override
    public StrategyEntity queryStrategyByStrategyId(Long strategyId) {
        //1.查询redis缓存
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntry = redisService.getValue(cacheKey);
        if (null != strategyEntry) {
            return strategyEntry;
        }
        //2.从库中查询策略配置,并缓存到redis中
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        StrategyEntity strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .ruleModel(strategy.getRuleModel())
                .strategyDesc(strategy.getStrategyDesc())
                .build();
        redisService.setValue(cacheKey, strategyEntity);

        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(ruleModel);
        strategyRule = strategyRuleDao.queryStrategyRule(strategyRule);
        return StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .ruleModel(strategyRule.getRuleModel())
                .ruleDesc(strategyRule.getRuleDesc())
                .ruleValue(strategyRule.getRuleValue())
                .ruleType(strategyRule.getRuleType())
                .awardId(strategyRule.getAwardId())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId,String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);
        return strategyRuleDao.queryStrategyRuleValue(strategyRule);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId, null,ruleModel);
    }

    @Override
    public RuleTreeVO queryRuleTreeByTreeId(String treeId) {
        //1.查询redis缓存
        String cacheKey = Constants.RedisKey.RULE_TREE_KEY + Constants.UNDERLINE + treeId;
        RuleTreeVO ruleTreeVO = redisService.getValue(cacheKey);
        if (null != ruleTreeVO) {
            return ruleTreeVO;
        }

        //2.从库中查询策略配置,并缓存到redis中
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeByTreeId(treeId);
        //类型转换
        List<RuleTreeNodeVO> ruleTreeNodeVOS = ruleTreeNodes.stream().map(ruleTreeNode -> {
            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOS = ruleTreeNodeLines.stream()
                    .map(ruleTreeNodeLine -> {
                        return RuleTreeNodeLineVO.builder()
                                .treeId(treeId)
                                .treeNodeIdFrom(ruleTreeNodeLine.getRuleNodeFrom())
                                .treeNodeIdTo(ruleTreeNodeLine.getRuleNodeTo())
                                .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                                .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                                .build();
                    })
                    .filter(ruleTreeNodeLineVO -> ruleTreeNodeLineVO.getTreeNodeIdFrom().equals(ruleTreeNode.getRuleKey()))
                    .collect(Collectors.toList());

            return RuleTreeNodeVO.builder()
                    .treeId(treeId)
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .treeNodeLinkList(ruleTreeNodeLineVOS)
                    .build();
        }).collect(Collectors.toList());

        HashMap<String, RuleTreeNodeVO> ruleTreeNodeMap = new HashMap<>();
        for (RuleTreeNodeVO ruleTreeNodeVO : ruleTreeNodeVOS) {
            String ruleKey = ruleTreeNodeVO.getRuleKey();
            ruleTreeNodeMap.put(ruleKey, ruleTreeNodeVO);
        }

        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);

        ruleTreeVO = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .ruleTreeRootNode(ruleTree.getTreeNodeRuleKey())
                .treeDesc(ruleTree.getTreeDesc())
                .treeName(ruleTree.getTreeName())
                .treeNodeMap(ruleTreeNodeMap)
                .build();
        redisService.setValue(cacheKey, ruleTreeVO);
        return ruleTreeVO;
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        String ruleModels = strategyAwardDao.queryStrategyAwardRuleModels(strategyAward);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModels).build();
    }

    @Override
    public void cacheStrategyAwardCount(String cacheKey, Integer awardCount) {
        if (redisService.isExists(cacheKey)) return;
        redisService.setAtomicLong(cacheKey, awardCount);
    }

    @Override
    public Boolean subtractAwardCount(String cacheKey) {
        return subtractAwardCount(cacheKey, null);
    }

    @Override
    public Boolean subtractAwardCount(String cacheKey, Date endTime) {
        long surplus = redisService.decr(cacheKey);
        if (surplus < 0) {
            redisService.setValue(cacheKey, 0L);
            return false;
        }
        String lockKey = cacheKey + "_" + surplus;
        Boolean lock = false;
        if (null != endTime){
            long expireTime = endTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
            lock = redisService.setNx(lockKey, expireTime, TimeUnit.MILLISECONDS);
        }else {
            lock = redisService.setNx(lockKey);
        }
        if (!lock) {
            log.info("策略奖品库存加锁失败");
        }
        return lock;
    }

    @Override
    public void awardStockConsumeSendQueue(StrategyAwardKeyStockVO strategyAwardKeyStockVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardKeyStockVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardKeyStockVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardKeyStockVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardKeyStockVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardKeyStockVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        return blockingQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        strategyAwardDao.updateStrategyAwardStock(strategyAward);
    }

    @Override
    public StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId + Constants.UNDERLINE + awardId;
        StrategyAwardEntity strategyAwardEntity = redisService.getValue(cacheKey);
        if (null != strategyAwardEntity) {
            return strategyAwardEntity;
        }
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        strategyAward = strategyAwardDao.queryStrategyAward(strategyAward);
        strategyAwardEntity = StrategyAwardEntity.builder()
                .strategyId(strategyId)
                .awardId(awardId)
                .awardTitle(strategyAward.getAwardTitle())
                .awardSubTitle(strategyAward.getAwardSubTitle())
                .awardCount(strategyAward.getAwardCount())
                .awardCountSurplus(strategyAward.getAwardCountSurplus())
                .sort(strategyAward.getSort())
                .awardRate(strategyAward.getAwardRate())
                .build();
        redisService.setValue(cacheKey, strategyAwardEntity);
        return strategyAwardEntity;
    }

    @Override
    public Integer queryTodayUserRaffleCount(String userId, Long strategyId) {
        Long activityId = raffleActivityDao.queryActivityIdByStrategyId(strategyId);
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setActivityId(activityId);
        raffleActivityAccountDay.setUserId(userId);
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDay(raffleActivityAccountDay);
        if (null == raffleActivityAccountDay) return 0;
        // 已抽奖次数 = 总次数 - 已抽次数
        return raffleActivityAccountDay.getDayCount() - raffleActivityAccountDay.getDayCountSurplus();
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return raffleActivityDao.queryStrategyIdByActivityId(activityId);
    }

    @Override
    public HashMap<String, Integer> queryRuleLockCount(String[] treeIds) {
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleLockCount(treeIds);
        HashMap<String, Integer> resultMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            String treeId = ruleTreeNode.getTreeId();
            Integer ruleCount = Integer.valueOf(ruleTreeNode.getRuleValue());
            resultMap.put(treeId, ruleCount);
        }
        return resultMap;
    }

    @Override
    public Integer queryActivityAccountTotalUseCount(String userId, Long strategyId) {
        Long activityId = raffleActivityDao.queryActivityIdByStrategyId(strategyId);
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccount(RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        // 返回计算使用量
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }

}
