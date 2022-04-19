package com.myself.bean.ods;

import com.sun.org.glassfish.gmbal.Description;
import lombok.Data;

/**
 * @author longyh
 * @Description: 动作(事件)
 * @analysis:
 * @date 2022/4/19 5:03 下午
 */
@Data
public class Action {

    /**
     * 动作id
     */
    private String actionId;

    /**
     * 目标id
     */
    private String item;

    /**
     * 目标类型
     */
    private String itemType;

    /**
     * 动作时间戳
     */
    private Long ts;

}

/**
 * "action_id": "favor_add",   --动作id
 * "item": "3",                   --目标id
 * "item_type": "sku_id",       --目标类型
 * "ts": 1585744376605           --动作时间戳
 * <p>
 * "actions":[{"action_id":"get_coupon","item":"2","item_type":"coupon_id","ts":1608281309818}]
 */
