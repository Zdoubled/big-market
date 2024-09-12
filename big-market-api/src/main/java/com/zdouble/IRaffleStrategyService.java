package com.zdouble;

import com.zdouble.dto.StrategyAwardListRequestDto;
import com.zdouble.dto.StrategyAwardListResponseDto;
import com.zdouble.dto.StrategyRaffleRequestDto;
import com.zdouble.dto.StrategyRaffleResponseDto;
import com.zdouble.types.model.Response;

import java.util.List;

public interface IRaffleStrategyService {
    // 加载抽奖策略装配缓存
    Response<Boolean> strategyArmory(Long strategyId);
    // 加载策略奖品缓存
    Response<List<StrategyAwardListResponseDto>> queryStrategyAwardList(StrategyAwardListRequestDto strategyAwardListRequestDto);
    // 执行抽奖操作
    Response<StrategyRaffleResponseDto> performRaffle(StrategyRaffleRequestDto strategyRaffleRequestDto);
}
