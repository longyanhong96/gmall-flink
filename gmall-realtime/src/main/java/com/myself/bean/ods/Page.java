package com.myself.bean.ods;

import lombok.Data;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/19 5:36 下午
 */
@Data
public class Page {

    /**
     * 持续时间毫秒
     */
    private int duringTime;

    /**
     * 目标id
     */
    private String item;

    /**
     * 目标类型
     */
    private String itemType;

    /**
     * 上页类型
     */
    private String lastPageId;

    /**
     * 页面ID
     */
    private String pageId;

    /**
     * 来源类型
     */
    private String sourceType;
}
/**
 * "during_time": 7648,        -- 持续时间毫秒
 * "item": "3",                  -- 目标id
 * "item_type": "sku_id",      -- 目标类型
 * "last_page_id": "login",    -- 上页类型
 * "page_id": "good_detail",   -- 页面ID
 * "sourceType": "promotion"   -- 来源类型
 */