package com.zdouble.types.common;

public class Constants {

    public final static String SPLIT = ",";
    public final static String SPACE = " ";
    public final static String COLON = ":";
    public final static String UNDERLINE = "_";

    //redis
    public static class RedisKey{
        /**
         * 策略相关key
         */
        public final static String STRATEGY_KEY = "big_market_strategy_key";
        public final static String STRATEGY_AWARD_KEY = "big_market_strategy_award_key";
        public final static String STRATEGY_AWARD_LIST_KEY = "big_market_strategy_award_list_key";
        public final static String STRATEGY_RATE_TABLE_KEY = "big_market_strategy_rate_table_key";
        public final static String STRATEGY_RATE_RANGE_KEY = "big_market_strategy_rate_range_key";

        public final static String STRATEGY_AWARD_COUNT_KEY = "big_market_strategy_count_key";
        public final static String STRATEGY_AWARD_COUNT_QUEUE_KEY = "big_market_strategy_count_queue_key";

        public final static String RULE_TREE_KEY = "big_market_rule_tree_key";

        /**
         * 活动相关key
         */
        public static final String ACTIVITY_KEY = "big_market_activity_key";
        public final static String ACTIVITY_SKU_KEY = "big_market_activity_sku_key";
        public static final String ACTIVITY_COUNT_KEY = "big_market_activity_count_key";
        public final static String ACTIVITY_SKU_STOCK_KEY = "big_market_activity_sku_stock_key";
        public static final String ACTIVITY_SKU_STOCK_LOCK_KEY = "big_market_activity_sku_stock_lock_key";
        public static final String ACTIVITY_SKU_STOCK_QUEUE_KEY = "big_market_sku_stock_queue_key";

        public static final String ACTIVITY_ACCOUNT_SURPLUS_COUNT_KEY = "big_market_activity_account_surplus_count_key";
        public static final String ACTIVITY_ACCOUNT_MONTH_SURPLUS_COUNT_KEY = "big_market_activity_account_month_surplus_count_key";
        public static final String ACTIVITY_ACCOUNT_DAY_SURPLUS_COUNT_KEY = "big_market_activity_account_day_surplus_count_key";

        /**
         * redis锁
         */
        public static final String ACTIVITY_ACCOUNT_SURPLUS_COUNT_LOCK = "big_market_activity_account_surplus_count_lock";
        public static final String USER_CREDIT_ACCOUNT_LOCK = "big_market_user_credit_account_lock";
        public static final String RAFFLE_ACTIVITY_ACCOUNT_LOCK = "big_market_raffle_activity_account_lock";

    }
}
