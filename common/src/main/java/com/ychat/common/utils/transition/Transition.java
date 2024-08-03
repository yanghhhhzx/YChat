package com.ychat.common.utils.transition;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Transition {

    //将List<string>转List<long>
    public static List<Long> StringListToLongList(List<String> stringList) {
        return stringList.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public static String DateToString(Date date) {
        // 创建一个SimpleDateFormat对象，定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 使用SimpleDateFormat对象的format方法将Date对象格式化为字符串
        String dateString = dateFormat.format(date);
        return dateString;
    }

    //将可根据，分割的string转为可编辑的list
    public static List<String> StringToList(String str) {

        return new ArrayList<>(Arrays.asList(str.split(",")));
    }


}
