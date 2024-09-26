package com.github.baiy.cadmin.common.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class Page<T> {
    // 总页数
    private Integer totalPages;
    // 总记录数
    private Long total;
    // 每页记录数
    private Integer pageSize;
    // 是否为最后一页
    private Boolean last;
    // 当前分页
    private Integer page;
    // 分页内容数据
    private List<T> lists;
    // 扩展数据
    private Map<String, ?> extend;

    public <U> Page<U> map(Function<T, U> converter) {
        var page = new Page<U>();
        page.setTotal(this.total);
        page.setTotalPages(this.totalPages);
        page.setPage(this.page);
        page.setPageSize(this.pageSize);
        page.setLast(this.last);
        page.setLists(this.lists.stream().map(converter).toList());
        page.setExtend(this.extend);
        return page;
    }
}
