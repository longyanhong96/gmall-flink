package com.myself.bean.ods;

import lombok.Data;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/19 5:08 下午
 */

@Data
public class Display {

    /**
     * 曝光类型
     */
    private String displayType;

    /**
     * 曝光对象id
     */
    private String item;

    /**
     * 曝光对象类型
     */
    private String itemType;

    /**
     * 出现顺序
     */
    private int order;

    /**
     * 曝光位置
     */
    private int posId;
}
/**
 * "displayType": "query",        -- 曝光类型
 * "item": "3",                     -- 曝光对象id
 * "item_type": "sku_id",         -- 曝光对象类型
 * "order": 1,                      --出现顺序
 * "pos_id": 2                      --曝光位置
 */