package com.myself.bean.ods;

import lombok.Data;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/19 5:41 下午
 */
@Data
public class StartLog {

    /**
     * 公共信息
     */
    private Common common;

    private Start start;

    private Err err;

    private Long ts;
}
