package com.zdouble.domain.activity.service.armory;

import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;

import java.util.Date;

public interface IActivityDispatch {
    Boolean subtractionSkuStock(Long sku, Date endDateTime);
}
