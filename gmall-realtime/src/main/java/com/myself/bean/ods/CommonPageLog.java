package com.myself.bean.ods;

import lombok.Data;

import java.util.List;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/19 5:33 下午
 */
@Data
public class CommonPageLog {

    /**
     * 公共信息
     */
    private Common common;

    /**
     * 动作(事件)
     */
    private List<Action> actions;

    /**
     * 曝光
     */
    private List<Display> displays;

    /**
     * 页面信息
     */
    private Page page;

    /**
     * 错误
     */
    private Err err;

    /**
     * 跳入时间戳
     */
    private Long ts;
}
