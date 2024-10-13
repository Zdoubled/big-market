package com.zdouble.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.zdouble.domain.credit.aggregate.UserCreditRechargeAggregate;
import com.zdouble.domain.credit.model.entity.UserCreditAccountEntity;
import com.zdouble.domain.credit.model.entity.UserCreditOrderEntity;
import com.zdouble.domain.credit.model.entity.UserCreditRechargeEntity;
import com.zdouble.domain.credit.repository.ICreditRepository;
import com.zdouble.infrastructure.persistent.dao.UserCreditAccountDao;
import com.zdouble.infrastructure.persistent.dao.UserCreditOrderDao;
import com.zdouble.infrastructure.persistent.po.UserCreditAccount;
import com.zdouble.infrastructure.persistent.po.UserCreditOrder;
import com.zdouble.infrastructure.persistent.redis.RedissonService;
import com.zdouble.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CreditRepository implements ICreditRepository {

    @Resource
    private UserCreditOrderDao userCreditOrderDao;
    @Resource
    private UserCreditAccountDao userCreditAccountDao;
    @Resource
    private IDBRouterStrategy routerStrategy;
    @Resource
    private TransactionTemplate executor;
    @Resource
    private RedissonService redissonService;


    @Override
    public void doUserCreditRecharge(UserCreditRechargeAggregate userCreditRechargeAggregate) {
        RLock lock = redissonService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userCreditRechargeAggregate.getUserId());
        try{
            lock.lock(3, TimeUnit.SECONDS);
            routerStrategy.doRouter(userCreditRechargeAggregate.getUserId());
            executor.execute(status -> {
                try {
                    UserCreditAccountEntity userCreditAccountEntity = userCreditRechargeAggregate.getUserCreditAccountEntity();
                    UserCreditAccount userCreditAccount = UserCreditAccount.builder()
                            .userId(userCreditAccountEntity.getUserId())
                            .totalAmount(userCreditAccountEntity.getTotalAmount())
                            .availableAmount(userCreditAccountEntity.getAvailableAmount())
                            .accountStatus(userCreditAccountEntity.getAccountStatus().getCode())
                            .build();
                    UserCreditAccount userCreditAccountReq = UserCreditAccount.builder()
                            .userId(userCreditAccount.getUserId())
                            .accountStatus(userCreditAccount.getAccountStatus())
                            .build();
                    userCreditAccountReq = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (null == userCreditAccountReq) {
                        userCreditAccountDao.insert(userCreditAccount);
                    }else {
                        userCreditAccountDao.updateUserCreditAccount(userCreditAccount);
                    }
                    UserCreditOrderEntity userCreditOrderEntity = userCreditRechargeAggregate.getUserCreditOrderEntity();
                    UserCreditOrder userCreditOrder = UserCreditOrder.builder()
                            .userId(userCreditOrderEntity.getUserId())
                            .orderId(userCreditOrderEntity.getOrderId())
                            .tradeName(userCreditOrderEntity.getTradeName())
                            .tradeType(userCreditOrderEntity.getTradeType().getCode())
                            .tradeAmount(userCreditOrderEntity.getTradeAmount())
                            .outBusinessNo(userCreditOrderEntity.getOutBusinessNo())
                            .build();
                    userCreditOrderDao.insert(userCreditOrder);
                }catch (DuplicateKeyException e){
                    log.error("用户行为返利积分充值订单已经存在", e);
                    status.setRollbackOnly();
                }catch (Exception e){
                    log.error("用户行为返利积分充值订单异常", e);
                    status.setRollbackOnly();
                }
                return 1;
            });
        }finally {
            lock.unlock();
            routerStrategy.clear();
        }
    }
}
