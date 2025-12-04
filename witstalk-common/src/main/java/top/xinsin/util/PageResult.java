package top.xinsin.util;

import java.util.List;

public record PageResult<T>(long page, long size, long total, List<T> records) {
    public static <T> PageResult<T> page(long page, long size, long total, List<T> records) {
        return new PageResult<>(page, size, total, records);
    }
}
