package com.github.baiy.cadmin.common.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Page {
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
    private List<?> lists;
    // 扩展数据
    private Map<String, ?> extend;
}
