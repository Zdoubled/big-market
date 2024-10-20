package com.zdouble.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.zdouble.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.zdouble.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.zdouble.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;
import com.zdouble.domain.activity.model.pojo.UserRaffleOrderStateVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.credit.model.entity.DeliveryOrderEntity;
import com.zdouble.infrastructure.event.EventPublisher;
import com.zdouble.infrastructure.persistent.dao.*;
import com.zdouble.infrastructure.persistent.po.*;
import com.zdouble.infrastructure.persistent.redis.IRedisService;
import com.zdouble.types.common.Constants;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.event.BaseEvent;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ActivityRepository implements IActivityRepository {
    @Resource
    private RaffleActivityOrderDao raffleActivityOrderDao;
    @Resource
    private RaffleActivityDao raffleActivityDao;
    @Resource
    private RaffleActivitySkuDao raffleActivitySkuDao;
    @Resource
    private RaffleActivityAccountDao raffleActivityAccountDao;
    @Resource
    private RaffleActivityCountDao raffleActivityCountDao;
    @Resource
    private IRedisService redisService;
    @Resource
    private IDBRouterStrategy dbRouterStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;
    @Resource
    private UserRaffleOrderDao userRaffleOrderDao;
    @Resource
    private RaffleActivityAccountMonthDao raffleActivityAccountMonthDao;
    @Resource
    private RaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        // 1.查缓存
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_KEY + sku;
        ActivitySkuEntity activitySkuEntity = redisService.getValue(cacheKey);
        if (null != activitySkuEntity) {
            return activitySkuEntity;
        }
        // 2.查数据库
        RaffleActivitySku activitySku = raffleActivitySkuDao.queryActivitySku(sku);
        activitySkuEntity = ActivitySkuEntity.builder()
                .activityCountId(activitySku.getActivityCountId())
                .activityId(activitySku.getActivityId())
                .sku(activitySku.getSku())
                .stockCount(activitySku.getStockCount())
                .stockCountSurplus(activitySku.getStockCountSurplus())
                .productAmount(activitySku.getProductAmount())
                .build();
        // 3.存缓存
        redisService.setValue(cacheKey, activitySkuEntity);
        return activitySkuEntity;
    }

    @Override
    public ActivityEntity queryActivityByActivityId(Long activityId) {
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if (null != activityEntity) {
            return activityEntity;
        }
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .strategyId(raffleActivity.getStrategyId())
                .activityDesc(raffleActivity.getActivityDesc())
                .activityName(raffleActivity.getActivityName())
                .state(raffleActivity.getState())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .build();
        redisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryActivityCountByActivityCountId(Long activityCountId) {
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if (null != activityCountEntity) {
            return activityCountEntity;
        }
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .totalCount(raffleActivityCount.getTotalCount())
                .build();
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        try {
            dbRouterStrategy.doRouter(createQuotaOrderAggregate.getUserId());
            // 构建订单对象
            ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            BeanUtils.copyProperties(activityOrderEntity, raffleActivityOrder, "state");
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());

            transactionTemplate.execute(status -> {
                try {
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                }catch(DuplicateKeyException e){
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    status.setRollbackOnly();
                }
                return 1;
            });
        }finally {
            dbRouterStrategy.clear();
        }
    }

    @Override
    public void doSaveNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        RLock lock = redisService.getLock(Constants.RedisKey.RAFFLE_ACTIVITY_ACCOUNT_LOCK + createQuotaOrderAggregate.getUserId());
        try{
            lock.lock(3, TimeUnit.SECONDS);
            // 构建订单对象
            ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            BeanUtils.copyProperties(activityOrderEntity, raffleActivityOrder, "state");
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            // 构建账户对象
            RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
            BeanUtils.copyProperties(activityOrderEntity, raffleActivityAccount);
            raffleActivityAccount.setTotalCountSurplus(activityOrderEntity.getTotalCount());
            raffleActivityAccount.setMonthCountSurplus(activityOrderEntity.getMonthCount());
            raffleActivityAccount.setDayCountSurplus(activityOrderEntity.getDayCount());
            dbRouterStrategy.doRouter(createQuotaOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                    int count = raffleActivityAccountDao.update(raffleActivityAccount);
                    if (count == 0){
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    // 更新月账户(存在则更新)
                    raffleActivityAccountMonthDao.addAccountQuota(raffleActivityAccount);
                    // 更新日账户(存在则更新)
                    raffleActivityAccountDayDao.addAccountQuota(raffleActivityAccount);
                    return 1;
                }catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
            });
        }finally {
            lock.unlock();
            dbRouterStrategy.clear();
        }
    }

    @Override
    public void updateOrder(DeliveryOrderEntity deliveryOrderEntity) {
        // 加锁更新活动用户账户
        RLock lock = redisService.getLock(Constants.RedisKey.RAFFLE_ACTIVITY_ACCOUNT_LOCK + deliveryOrderEntity.getUserId());
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouterStrategy.doRouter(deliveryOrderEntity.getUserId());
            // 查询订单
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            raffleActivityOrder.setUserId(deliveryOrderEntity.getUserId());
            raffleActivityOrder.setOutBusinessNo(deliveryOrderEntity.getOutBusinessNo());
            raffleActivityOrder = raffleActivityOrderDao.queryRaffleActivityOrder(raffleActivityOrder);

            // 创建用户活动总账户对象
            RaffleActivityAccount raffleActivityAccountReq = new RaffleActivityAccount();
            raffleActivityAccountReq.setUserId(raffleActivityOrder.getUserId());
            raffleActivityAccountReq.setActivityId(raffleActivityOrder.getActivityId());
            raffleActivityAccountReq.setTotalCount(raffleActivityOrder.getTotalCount());
            raffleActivityAccountReq.setTotalCountSurplus(raffleActivityOrder.getTotalCount());
            raffleActivityAccountReq.setMonthCount(raffleActivityOrder.getMonthCount());
            raffleActivityAccountReq.setMonthCountSurplus(raffleActivityOrder.getMonthCount());
            raffleActivityAccountReq.setDayCount(raffleActivityOrder.getDayCount());
            raffleActivityAccountReq.setDayCountSurplus(raffleActivityOrder.getDayCount());
            // 创建用户活动月账户对象
            RaffleActivityAccountMonth raffleActivityAccountMonthReq = new RaffleActivityAccountMonth();
            raffleActivityAccountMonthReq.setUserId(raffleActivityOrder.getUserId());
            raffleActivityAccountMonthReq.setActivityId(raffleActivityOrder.getActivityId());
            raffleActivityAccountMonthReq.setMonthCount(raffleActivityOrder.getMonthCount());
            raffleActivityAccountMonthReq.setMonthCountSurplus(raffleActivityOrder.getMonthCount());
            // 创建用户活动日账户对象
            RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
            raffleActivityAccountDayReq.setUserId(raffleActivityOrder.getUserId());
            raffleActivityAccountDayReq.setActivityId(raffleActivityOrder.getActivityId());
            raffleActivityAccountDayReq.setDayCount(raffleActivityOrder.getDayCount());
            raffleActivityAccountDayReq.setDayCountSurplus(raffleActivityOrder.getDayCount());
            transactionTemplate.execute(status -> {
                try {
                    // 修改订单状态
                    RaffleActivityOrder raffleActivityOrderReq = new RaffleActivityOrder();
                    raffleActivityOrderReq.setUserId(deliveryOrderEntity.getUserId());
                    raffleActivityOrderReq.setOutBusinessNo(deliveryOrderEntity.getOutBusinessNo());
                    int result = raffleActivityOrderDao.updateOrderCompleted(raffleActivityOrderReq);
                    if (result == 0){
                        status.setRollbackOnly();
                        return 1;
                    }
                    // 更新用户活动总账户
                    RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccount(raffleActivityAccountReq);
                    if (null == raffleActivityAccount){
                        raffleActivityAccountDao.insert(raffleActivityAccountReq);
                    }else{
                        raffleActivityAccountDao.update(raffleActivityAccountReq);
                    }
                    // 更新用户活动月账户
                    raffleActivityAccountMonthDao.addAccountQuota(raffleActivityAccount);
                    // 更新用户活动日账户
                    raffleActivityAccountDayDao.addAccountQuota(raffleActivityAccount);
                }catch (DuplicateKeyException e){
                    log.error("订单已经存在 userId: {} outBusinessNo: {}", deliveryOrderEntity.getUserId(), deliveryOrderEntity.getOutBusinessNo());
                    status.setRollbackOnly();
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), "订单已经存在");
                }
                return 1;
            });
        }finally {
            lock.unlock();
            dbRouterStrategy.clear();
        }
    }

    @Override
    public UnpaidActivityOrderEntity queryUnpaidActivityOrder(ActivitySkuChargeEntity activitySkuChargeEntity) {
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(activitySkuChargeEntity.getUserId());
        raffleActivityOrder.setSku(activitySkuChargeEntity.getSku());
        raffleActivityOrder = raffleActivityOrderDao.queryUnpaidActivityOrder(raffleActivityOrder);
        if (null == raffleActivityOrder) return null;
        return UnpaidActivityOrderEntity.builder()
                .userId(raffleActivityOrder.getUserId())
                .orderId(raffleActivityOrder.getOrderId())
                .outBusinessNo(raffleActivityOrder.getOutBusinessNo())
                .payAmount(raffleActivityOrder.getPayAmount())
                .build();
    }

    @Override
    public void cacheActivitySkuCount(String cacheKey, Integer stockCont) {
        redisService.setAtomicLong(cacheKey, stockCont);
    }

    @Override
    public Boolean subtractionSkuStock(String cacheKey, Date endDateTime, Long sku) {
        // 执行方法 : redis decr 并 lock，保证库存不超卖
        // 1. 根据key扣减sku库存
        long surplus = redisService.decr(cacheKey);
        if (surplus == 0){
            // 发送消息队列，通知清空异步待处理的订单（sku）
            String topic = activitySkuStockZeroMessageEvent.topic();
            BaseEvent.EventMessage<Long> eventMessage = activitySkuStockZeroMessageEvent.buildEventMessage(sku);
            eventPublisher.publish(topic, eventMessage);
        }else if (surplus < 0){
            redisService.setAtomicLong(cacheKey, 0);
            return false;
        }
        // 2.1 加锁
        String lockKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_LOCK_KEY + surplus;
        // 2.2 设置过期时间，直到活动结束
        long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean lock = redisService.setNx(lockKey, expireMillis, TimeUnit.MILLISECONDS);
        if (!lock){
            log.info("活动sku库存扣减lock失败:{}", lockKey);
        }
        return true;
    }

    @Override
    public void activitySkuConsumeSendQueue(ActivitySkuStockVO activitySkuStockVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY + activitySkuStockVO.getSku();
        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<Object> delayedQueue = redisService.getDelayedQueue(blockingQueue);

        delayedQueue.offer(activitySkuStockVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockVO takeQueueValue(Long sku) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY + sku;
        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue(cacheKey);

        return (ActivitySkuStockVO)blockingQueue.poll();
    }

    @Override
    public void updateSkuStock(Long sku, Long activityId) {
        raffleActivitySkuDao.updateSkuStock(sku, activityId);
    }

    @Override
    public void clearQueueValue(Long sku) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY + sku;
        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue(cacheKey);
        redisService.getDelayedQueue(blockingQueue).clear();
        blockingQueue.clear();
    }

    @Override
    public void updateSkuStockZero(Long sku) {
        raffleActivitySkuDao.updateSkuStockZero(sku);
    }

    @Override
    public UserRaffleOrderEntity queryUserRaffleOrder(Long activityId, String userId) {
        UserRaffleOrder userRaffleOrder = userRaffleOrderDao.queryUserRaffleOrder(activityId, userId);
        if (null == userRaffleOrder) return null;
        return UserRaffleOrderEntity.builder()
                .userId(userRaffleOrder.getUserId())
                .orderId(userRaffleOrder.getOrderId())
                .orderState(UserRaffleOrderStateVO.valueOf(userRaffleOrder.getOrderState()))
                .orderTime(userRaffleOrder.getOrderTime())
                .activityId(userRaffleOrder.getActivityId())
                .activityName(userRaffleOrder.getActivityName())
                .strategyId(userRaffleOrder.getStrategyId())
                .build();
    }

    @Override
    public ActivityAccountEntity queryActivityAccount(String userId, Long activityId) {
        // 封装条件查询
        RaffleActivityAccount raffleActivityAccountReq = new RaffleActivityAccount();
        raffleActivityAccountReq.setUserId(userId);
        raffleActivityAccountReq.setActivityId(activityId);
        raffleActivityAccountReq = raffleActivityAccountDao.queryActivityAccount(raffleActivityAccountReq);
        if (null == raffleActivityAccountReq) return null;
        // 缓存用户总、月、日剩余额度
        String cacheKey = Constants.RedisKey.ACTIVITY_ACCOUNT_SURPLUS_COUNT_KEY + activityId + Constants.UNDERLINE + userId;
        String cacheMonthKey = Constants.RedisKey.ACTIVITY_ACCOUNT_MONTH_SURPLUS_COUNT_KEY + activityId + Constants.UNDERLINE + userId;
        String cacheDayKey = Constants.RedisKey.ACTIVITY_ACCOUNT_DAY_SURPLUS_COUNT_KEY + activityId + Constants.UNDERLINE + userId;
        String lockKey = Constants.RedisKey.ACTIVITY_ACCOUNT_SURPLUS_COUNT_LOCK + activityId + Constants.UNDERLINE + userId;
        if (!redisService.isExists(cacheKey)) {
            RLock lock = redisService.getLock(lockKey);
            try {
                lock.lock(2, TimeUnit.SECONDS);
                if (!redisService.isExists(cacheKey)){
                    redisService.setAtomicLong(cacheKey, raffleActivityAccountReq.getTotalCountSurplus());
                    redisService.setAtomicLong(cacheMonthKey, raffleActivityAccountReq.getMonthCountSurplus());
                    redisService.setAtomicLong(cacheDayKey, raffleActivityAccountReq.getDayCountSurplus());
                }
            }finally {
                lock.unlock();
            }
        }
        return ActivityAccountEntity.builder()
                .userId(raffleActivityAccountReq.getUserId())
                .activityId(raffleActivityAccountReq.getActivityId())
                .totalCount(raffleActivityAccountReq.getTotalCount())
                .totalCountSurplus(raffleActivityAccountReq.getTotalCountSurplus())
                .monthCount(raffleActivityAccountReq.getMonthCount())
                .monthCountSurplus(raffleActivityAccountReq.getMonthCountSurplus())
                .dayCount(raffleActivityAccountReq.getDayCount())
                .dayCountSurplus(raffleActivityAccountReq.getDayCountSurplus())
                .build();
        }

    @Override
    public void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        try{
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
            ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
            // 先对redis中的剩余额度进行扣减
            String lockKey = Constants.RedisKey.ACTIVITY_ACCOUNT_SURPLUS_COUNT_LOCK + activityId + Constants.UNDERLINE + userId;
            RLock lock = redisService.getLock(lockKey);
            try {
                lock.lock(2, TimeUnit.SECONDS);
                long countSurplus = redisService.decr(Constants.RedisKey.ACTIVITY_ACCOUNT_SURPLUS_COUNT_KEY + activityId + Constants.UNDERLINE + userId);
                long monthCountSurplus = redisService.decr(Constants.RedisKey.ACTIVITY_ACCOUNT_MONTH_SURPLUS_COUNT_KEY + activityId + Constants.UNDERLINE + userId);
                long dayCountSurplus = redisService.decr(Constants.RedisKey.ACTIVITY_ACCOUNT_DAY_SURPLUS_COUNT_KEY + activityId + Constants.UNDERLINE + userId);
                if (countSurplus < 0 || monthCountSurplus < 0 || dayCountSurplus < 0){
                    throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                }
            }finally {
                lock.unlock();
            }

            dbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                // 更新总账户的额度
                int totalCount = raffleActivityAccountDao.updateActivityAccountSubtractionQuota(RaffleActivityAccount.builder()
                        .activityId(activityId)
                        .userId(userId)
                        .build()
                );
                if (1 != totalCount){
                    status.setRollbackOnly();
                    log.info("更新总账户额度失败:{}", totalCount);
                    throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                }
                // 更新月账户的额度
                if (!createPartakeOrderAggregate.getExistMonth()){
                    // 不存在,插入
                    raffleActivityAccountMonthDao.insertActivityAccountAccountMonth(RaffleActivityAccountMonth.builder()
                            .userId(userId)
                            .activityId(activityId)
                            .monthCount(activityAccountMonthEntity.getMonthCount())
                            .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus())
                            .month(activityAccountMonthEntity.getMonth())
                            .build()
                    );
                }
                int monthCount = raffleActivityAccountMonthDao.updateActivityAccountAccountMonthSubtractionQuota(RaffleActivityAccountMonth.builder()
                        .activityId(activityId)
                        .userId(userId)
                        .month(activityAccountMonthEntity.getMonth())
                        .build()
                );
                if (1 != monthCount){
                    status.setRollbackOnly();
                    log.info("更新月账户额度失败:{}", monthCount);
                    throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                }
                // 扣减总账户月额度镜像
                raffleActivityAccountDao.updateActivityAccountMonthSurplusMirror(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                                .build()
                );

                // 更新日账户的额度
                if (!createPartakeOrderAggregate.getExistDay()){
                    // 不存在,插入
                    raffleActivityAccountDayDao.insertActivityAccountAccountDay(RaffleActivityAccountDay.builder()
                            .userId(userId)
                            .activityId(activityId)
                            .dayCount(activityAccountDayEntity.getDayCount())
                            .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus())
                            .day(activityAccountDayEntity.getDay())
                            .build()
                    );
                }
                int dayCount = raffleActivityAccountDayDao.updateActivityAccountAccountDaySubtractionQuota(RaffleActivityAccountDay.builder()
                        .activityId(activityId)
                        .userId(userId)
                        .day(activityAccountDayEntity.getDay())
                        .build()
                );
                if (1 != dayCount){
                    status.setRollbackOnly();
                    log.info("更新日账户额度失败:{}", dayCount);
                    throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                }

                // 扣减总账户日额度镜像
                raffleActivityAccountDao.updateActivityAccountDaySurplusMirror(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                                .build()
                );
                UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();
                try {
                    userRaffleOrderDao.insert(UserRaffleOrder.builder()
                            .activityId(activityId)
                            .userId(userId)
                            .orderId(userRaffleOrderEntity.getOrderId())
                            .orderTime(userRaffleOrderEntity.getOrderTime())
                            .orderState(userRaffleOrderEntity.getOrderState().getCode())
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .activityName(userRaffleOrderEntity.getActivityName())
                            .build()
                    );
                }catch (AppException e){
                    log.info("插入用户抽奖订单失败:{}", e.getMessage());
                    status.setRollbackOnly();
                    throw new AppException(ResponseCode.USER_ORDER_INSERT_ERROR.getCode(), ResponseCode.USER_ORDER_INSERT_ERROR.getInfo());
                }catch (DuplicateKeyException e){
                    log.info("插入用户抽奖订单失败:{}", e.getMessage());
                    status.setRollbackOnly();
                    throw new AppException(ResponseCode.USER_ORDER_INSERT_ERROR.getCode(), ResponseCode.USER_ORDER_INSERT_ERROR.getInfo());
                }
                return 1;
            });
        }finally {
            dbRouterStrategy.clear();
        }

    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonth(String userId, Long activityId, String month) {
        RaffleActivityAccountMonth activityAccountMonth = RaffleActivityAccountMonth.builder()
                .userId(userId)
                .activityId(activityId)
                .month(month)
                .build();
        activityAccountMonth = raffleActivityAccountMonthDao.queryActivityAccountMonth(activityAccountMonth);
        if (null != activityAccountMonth){
            return ActivityAccountMonthEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .monthCount(activityAccountMonth.getMonthCount())
                    .monthCountSurplus(activityAccountMonth.getMonthCountSurplus())
                    .month(month)
                    .build();
        }
        return null;
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDay(String userId, Long activityId, String day) {
        RaffleActivityAccountDay activityAccountDay = RaffleActivityAccountDay.builder()
                .activityId(activityId)
                .userId(userId)
                .day(day)
                .build();
        activityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDay(activityAccountDay);
        if (null != activityAccountDay){
            return ActivityAccountDayEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .dayCount(activityAccountDay.getDayCount())
                    .dayCountSurplus(activityAccountDay.getDayCountSurplus())
                    .day(day)
                    .build();
        }
        return null;
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkuList = raffleActivitySkuDao.queryActivitySkuByActivityId(activityId);
        return raffleActivitySkuList.stream().map(raffleActivitySku -> {
            return ActivitySkuEntity.builder()
                    .activityId(raffleActivitySku.getActivityId())
                    .activityCountId(raffleActivitySku.getActivityCountId())
                    .sku(raffleActivitySku.getSku())
                    .stockCount(raffleActivitySku.getStockCount())
                    .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                    .productAmount(raffleActivitySku.getProductAmount())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Long queryStrategyIdByActivityId(Long articleId) {
        return raffleActivityDao.queryStrategyIdByActivityId(articleId);
    }

    @Override
    public Integer queryRaffleActivityTotalPartakeCount(String userId, Long activityId) {
        RaffleActivityAccount activityAccount = RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build();
        activityAccount = raffleActivityAccountDao.queryActivityAccount(activityAccount);
        if (null == activityAccount) return 0;
        return activityAccount.getTotalCount() - activityAccount.getTotalCountSurplus();
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuList() {
        List<RaffleActivitySku> raffleActivitySkuList = raffleActivitySkuDao.queryActivitySkuList();
        return raffleActivitySkuList.stream().map(raffleActivitySku -> {
            return ActivitySkuEntity.builder()
                    .sku(raffleActivitySku.getSku())
                    .activityId(raffleActivitySku.getActivityId())
                    .activityCountId(raffleActivitySku.getActivityCountId())
                    .stockCount(raffleActivitySku.getStockCount())
                    .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                    .productAmount(raffleActivitySku.getProductAmount())
                    .build();
        }).collect(Collectors.toList());
    }


}
