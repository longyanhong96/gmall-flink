package com.myself.bean.ods;

import lombok.Data;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/19 5:12 下午
 */
@Data
public class Start {
    /**
     * icon手机图标  notice 通知   install 安装后启动
     */
    private String entry;

    /**
     * 启动加载时间
     */
    private int loadingTime;

    /**
     * 广告页ID
     */
    private int openAdId;

    /**
     * 广告总共播放时间
     */
    private int openAdMs;

    /**
     * 用户跳过广告时点
     */
    private int openAdSkipMs;
}

/**
 * "entry": "icon",         --icon手机图标  notice 通知   install 安装后启动
 * "loading_time": 18803,  --启动加载时间
 * "open_ad_id": 7,        --广告页ID
 * "open_ad_ms": 3449,    -- 广告总共播放时间
 * "open_ad_skip_ms": 1989   --  用户跳过广告时点
 */
