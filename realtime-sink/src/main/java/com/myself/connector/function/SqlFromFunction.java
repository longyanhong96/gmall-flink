package com.myself.connector.function;

import java.io.Serializable;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 6:03 下午
 */
public interface SqlFromFunction<T> extends Serializable {

    /**
     * bean 转化insert sql
     * @param bean
     * @return sql
     */
    public String transformSql(T bean);
}
