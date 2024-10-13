package com.zdouble.domain.credit.service;

import com.zdouble.domain.award.model.vo.UserCreditAccountStatusVO;
import com.zdouble.domain.credit.aggregate.UserCreditRechargeAggregate;
import com.zdouble.domain.credit.model.entity.UserCreditAccountEntity;
import com.zdouble.domain.credit.model.entity.UserCreditOrderEntity;
import com.zdouble.domain.credit.model.entity.UserCreditRechargeEntity;
import com.zdouble.domain.credit.model.vo.TradeTypeVO;
import com.zdouble.domain.credit.repository.ICreditRepository;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
@Slf4j
public class CreditService implements ICreditService {

    @Resource
    private ICreditRepository creditRepository;

    @Override
    public String createCreditRechargeOrder(UserCreditRechargeEntity userCreditRechargeEntity) {
        // 参数校验
        String userId = userCreditRechargeEntity.getUserId();
        BigDecimal creditRecharge = userCreditRechargeEntity.getCreditRecharge();
        String outBusinessNo = userCreditRechargeEntity.getOutBusinessNo();
        if (StringUtils.isBlank(userId) || creditRecharge == null || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 创建userCreditAccountEntity
        UserCreditAccountEntity userCreditAccountEntity = UserCreditAccountEntity.builder()
                .userId(userId)
                .totalAmount(creditRecharge)
                .availableAmount(creditRecharge)
                .accountStatus(UserCreditAccountStatusVO.open)
                .build();
        // 创建userCreditOrder
        UserCreditOrderEntity userCreditOrderEntity = UserCreditOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeName("行为返利")
                .tradeType(TradeTypeVO.forward)
                .tradeAmount(creditRecharge)
                .outBusinessNo(outBusinessNo)
                .build();
        // 创建聚合对象
        UserCreditRechargeAggregate userCreditRechargeAggregate = UserCreditRechargeAggregate.builder()
                .userId(userId)
                .userCreditAccountEntity(userCreditAccountEntity)
                .userCreditOrderEntity(userCreditOrderEntity)
                .build();
        // 保存
        creditRepository.doUserCreditRecharge(userCreditRechargeAggregate);

        // 保存返回订单id
        return userCreditOrderEntity.getOrderId();
    }
}
