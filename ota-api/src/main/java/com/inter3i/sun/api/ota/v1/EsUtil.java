package com.inter3i.sun.api.ota.v1;


import java.util.*;

/**
 * Created by 刘晓磊 on 2016/11/23.
 */
@SuppressWarnings("DefaultFileTemplate")
public class EsUtil {
    private int date = 0;
    public String EsResultSort(Map<Integer,Integer> mapSort,StringBuilder content){
        Set<Integer> keySet = mapSort.keySet();
        for (Integer key : keySet) {
            content.insert(key + date, "<em>");
            date += 4;
            content.insert(mapSort.get(key) + date, "</em>");
            date += 5;
        }
        return content.toString();
    }

}
