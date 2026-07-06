package com.uas.demo.dto;

import java.util.List;

/**
 * 统一分页响应格式（与 uas-common 的 PageResult 保持一致）。
 */
public class PageResult<T> {

    private long total;
    private List<T> records;

    public PageResult() {}

    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
}
