package com.community.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 *
 * @param <T> 数据类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    public PageResult(List<T> list, Integer page, Integer pageSize, Long total) {
        this.list = list;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.hasNext = (long) page * pageSize < total;
    }
}
