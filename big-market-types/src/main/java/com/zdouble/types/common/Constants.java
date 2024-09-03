package com.zdouble.types.common;

public class Constants {

    public final static String SPLIT = ",";
    public final static String SPACE = " ";
    public final static String COLON = ":";
    public final static String UNDERLINE = "_";

    //redis
    public static class RedisKey{
        public final static String STRATEGY_KEY = "big_market_strategy_key";
        public final static String STRATEGY_AWARD_KEY = "big_market_strategy_award_key";
        public final static String STRATEGY_AWARD_LIST_KEY = "big_market_strategy_award_list_key";
        public final static String STRATEGY_RATE_TABLE_KEY = "big_market_strategy_rate_table_key";
        public final static String STRATEGY_RATE_RANGE_KEY = "big_market_strategy_rate_range_key";

        public final static String STRATEGY_AWARD_COUNT_KEY = "big_market_strategy_count_key";
        public final static String STRATEGY_AWARD_COUNT_QUEUE_KEY = "big_market_strategy_count_queue_key";

        public final static String RULE_TREE_KEY = "big_market_rule_tree_key";
    }
}
