package com.zdouble.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.zdouble.domain.credit.aggregate.TradeAggregate;
import com.zdouble.domain.credit.model.entity.TaskEntity;
import com.zdouble.domain.credit.model.entity.UserCreditAccountEntity;
import com.zdouble.domain.credit.model.entity.UserCreditOrderEntity;
import com.zdouble.domain.credit.repository.ICreditRepository;
import com.zdouble.infrastructure.event.EventPublisher;
import com.zdouble.infrastructure.persistent.dao.TaskDao;
import com.zdouble.infrastructure.persistent.dao.UserCreditAccountDao;
import com.zdouble.infrastructure.persistent.dao.UserCreditOrderDao;
import com.zdouble.infrastructure.persistent.po.Task;
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
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CreditRepository implements ICreditRepository {

    @Resource
    private UserCreditOrderDao userCreditOrderDao;
    @Resource
    private UserCreditAccountDao userCreditAccountDao;
    @Resource
    private TaskDao taskDao;
    @Resource
    private IDBRouterStrategy routerStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private RedissonService redissonService;
    @Resource
    private EventPublisher eventPublisher;


    @Override
    public void doSaveUserCreditAdjust(TradeAggregate tradeAggregate) {
        RLock lock = redissonService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + tradeAggregate.getUserId());
        try{
            lock.lock(3, TimeUnit.SECONDS);
            routerStrategy.doRouter(tradeAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    /**
                     * 执行积分增减操作
                     */
                    // 类型转换
                    UserCreditAccountEntity userCreditAccountEntity = tradeAggregate.getUserCreditAccountEntity();
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
                    // 更新积分账户，不存在则创建
                    if (null == userCreditAccountReq) {
                        userCreditAccountDao.insert(userCreditAccount);
                    }else {
                        userCreditAccountDao.updateUserCreditAccount(userCreditAccount);
                    }
                    // 类型转换
                    UserCreditOrderEntity userCreditOrderEntity = tradeAggregate.getUserCreditOrderEntity();
                    UserCreditOrder userCreditOrder = UserCreditOrder.builder()
                            .userId(userCreditOrderEntity.getUserId())
                            .orderId(userCreditOrderEntity.getOrderId())
                            .tradeName(userCreditOrderEntity.getTradeName().getCode())
                            .tradeType(userCreditOrderEntity.getTradeType().getCode())
                            .tradeAmount(userCreditOrderEntity.getTradeAmount())
                            .outBusinessNo(userCreditOrderEntity.getOutBusinessNo())
                            .build();
                    userCreditOrderDao.insert(userCreditOrder);
                    // task 类型转换
                    TaskEntity taskEntity = tradeAggregate.getTaskEntity();
                    Task task = Task.builder()
                            .userId(taskEntity.getUserId())
                            .topic(taskEntity.getTopic())
                            .messageId(taskEntity.getMessageId())
                            .message(JSON.toJSONString(taskEntity.getMessage()))
                            .state(taskEntity.getState().getCode())
                            .build();
                    taskDao.insertTask(task);
                    eventPublisher.publish(task.getTopic(), task.getMessage());
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

    @Override
    public BigDecimal queryCreditAvailableByUserId(String userId) {
        return userCreditAccountDao.queryCreditAvailableByUserId(userId);
    }
}
