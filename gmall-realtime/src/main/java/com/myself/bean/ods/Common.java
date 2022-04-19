package com.myself.bean.ods;

import lombok.Data;

/**
 * @author longyh
 * @Description: 公共信息
 * @analysis:
 * @date 2022/4/19 4:56 下午
 */
@Data
public class Common {

    /**
     * 地区编码
     */
    private String ar;

    /**
     * 手机品牌
     */
    private String ba;

    /**
     * 渠道
     */
    private String ch;

    /**
     * 是否首日使用，首次使用的当日，该字段值为1，过了24:00，该字段置为0。
     */
    private String isNew;

    /**
     * 手机型号
     */
    private String md;

    /**
     * 设备id
     */
    private String mid;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 会员id
     */
    private String uid;

    /**
     * app版本号
     */
    private String vc;
}

/**
 * {
 * "common": {                  -- 公共信息
 * "ar": "230000",              -- 地区编码
 * "ba": "iPhone",              -- 手机品牌
 * "ch": "Appstore",            -- 渠道
 * "is_new": "1",--是否首日使用，首次使用的当日，该字段值为1，过了24:00，该字段置为0。
 * "md": "iPhone 8",            -- 手机型号
 * "mid": "YXfhjAYH6As2z9Iq", -- 设备id
 * "os": "iOS 13.2.9",          -- 操作系统
 * "uid": "485",                 -- 会员id
 * "vc": "v2.1.134"             -- app版本号
 * },
 */