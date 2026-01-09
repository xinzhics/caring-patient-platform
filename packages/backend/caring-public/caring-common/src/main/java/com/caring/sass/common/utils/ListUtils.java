package com.caring.sass.common.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ListUtils {


    /**
     * 将 list 分割成 subSize 长度的多个数组。
     *
     * @return
     * @Author: 杨帅
     * @Date: 2020/4/17 10:13
     */
    public static <T> List<List<T>> subList(List<T> list, int subSize) {
        if (Objects.isNull(list) || subSize <= 0) {
            return new ArrayList<>();
        }
        int size = list.size();
        int toIndex = 0;
        List<List<T>> lists = new ArrayList<>();
        for (int fromIndex = 0; fromIndex < size; ) {
            if (toIndex < size && toIndex + subSize < size) {
                toIndex += subSize;
            } else {
                toIndex = size;
            }
            List<T> stringList = list.subList(fromIndex, toIndex);
            lists.add(stringList);
            if (fromIndex + subSize < size) {
                fromIndex += subSize;
            } else {
                fromIndex = size;
            }
        }
        return lists;
    }

    public static String getSqlIdsJoin(List longs) {

        StringBuilder buf = new StringBuilder();
        Iterator<Object> iterator = longs.iterator();
        while(iterator.hasNext()) {
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append("'");
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj).append("'");
            }
        }
        return buf.toString();

    }

}