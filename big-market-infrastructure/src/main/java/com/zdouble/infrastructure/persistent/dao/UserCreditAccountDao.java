package com.zdouble.infrastructure.persistent.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.zdouble.infrastructure.persistent.po.UserCreditAccount;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

/**
* @author 曾庆达
* @description 针对表【user_credit_account(用户积分账户)】的数据库操作Mapper
* @createDate 2024-10-10 15:44:51
* @Entity com.zdouble.infrastructure.persistent.po.UserCreditAccount
*/
@Mapper
public interface UserCreditAccountDao {

    int updateUserCreditAccount(UserCreditAccount userCreditAccount);
    void insert(UserCreditAccount creditAccount);

    UserCreditAccount queryUserCreditAccount(UserCreditAccount userCreditAccount);

    @DBRouter
    BigDecimal queryCreditAvailableByUserId(String userId);
}




