package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.zdouble.infrastructure.persistent.po.Award;
import com.zdouble.infrastructure.persistent.po.StrategyAward;
import com.zdouble.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StrategyAwardDao {
    List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);

    void updateStrategyAwardStock(StrategyAward strategyAward);

    String queryStrategyAwardRuleModels(StrategyAward strategyAward);

    StrategyAward queryStrategyAward(StrategyAward strategyAward);
}
