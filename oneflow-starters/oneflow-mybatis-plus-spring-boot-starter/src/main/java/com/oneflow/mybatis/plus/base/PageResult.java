package com.oneflow.mybatis.plus.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Accessors(chain = true)
@Data
public class PageResult<T> implements Serializable {

    /**
     * 页号
     */
    private long page = 1;

    /**
     * 分页大小
     */
    private long pageSize = 10;

    /**
     * 总条数
     */
    private long total;

    /**
     * 结果集
     */
    private List<T> result = Collections.emptyList();

    public PageResult(IPage<T> iPage) {
        this.page = iPage.getCurrent();
        this.pageSize = iPage.getSize();
        this.total = iPage.getTotal();
        this.result = iPage.getRecords();
    }
}