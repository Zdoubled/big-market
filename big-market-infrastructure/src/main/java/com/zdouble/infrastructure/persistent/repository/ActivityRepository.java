package com.zdouble.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.zdouble.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.zdouble.domain.activity.model.aggregate.CreateOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
                .createTime(activitySku.getCreateTime())
                .updateTime(activitySku.getUpdateTime())
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
                .updateTime(raffleActivity.getUpdateTime())
                .createTime(raffleActivity.getCreateTime())
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
                .createTime(raffleActivityCount.getCreateTime())
                .updateTime(raffleActivityCount.getUpdateTime())
                .build();
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public void saveOrderAggregate(CreateOrderAggregate createOrderAggregate) {
            // 构建订单对象
        ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
        raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
        raffleActivityOrder.setDayCount(activityOrderEntity.getDayCount());
        raffleActivityOrder.setMonthCount(activityOrderEntity.getMonthCount());
        raffleActivityOrder.setTotalCount(activityOrderEntity.getTotalCount());
        raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());
        raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
        raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
        raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
        raffleActivityOrder.setSku(activityOrderEntity.getSku());
        raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
        // 构建账户对象
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setActivityId(activityOrderEntity.getActivityId());
        raffleActivityAccount.setUserId(createOrderAggregate.getUserId());
        raffleActivityAccount.setDayCount(createOrderAggregate.getDayCount());
        raffleActivityAccount.setMonthCount(createOrderAggregate.getMonthCount());
        raffleActivityAccount.setTotalCount(createOrderAggregate.getTotalCount());
        raffleActivityAccount.setDayCountSurplus(createOrderAggregate.getDayCountSurplus());
        raffleActivityAccount.setMonthCountSurplus(createOrderAggregate.getMonthCountSurplus());
        raffleActivityAccount.setTotalCountSurplus(createOrderAggregate.getTotalCountSurplus());
        try{
            dbRouterStrategy.doRouter(createOrderAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                    int count = raffleActivityAccountDao.update(raffleActivityAccount);
                    if (count == 0){
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    return 1;
                }catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
        });
        }catch (Exception e) {
            dbRouterStrategy.clear();
        }
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
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<Object> delayedQueue = redisService.getDelayedQueue(blockingQueue);

        delayedQueue.offer(activitySkuStockVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue(cacheKey);

        return (ActivitySkuStockVO)blockingQueue.poll();
    }

    @Override
    public void updateSkuStock(Long sku, Long activityId) {
        raffleActivitySkuDao.updateSkuStock(sku, activityId);
    }

    @Override
    public void clearQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue(cacheKey);
        redisService.getDelayedQueue(blockingQueue).clear();
        blockingQueue.clear();
    }

    @Override
    public void updateSkuStockZero(Long sku) {
        raffleActivitySkuDao.updateSkuStockZero(sku);
    }
}
