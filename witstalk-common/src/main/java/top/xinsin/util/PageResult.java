package top.xinsin.util;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long total;
    private List<T> records;
    private long page;
    private long size;

    public static <T> PageResult<T> page(long page, long size, long total, List<T> records) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setTotal(total);
        pageResult.setRecords(records);
        return pageResult;

    }
}
