/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/04/21
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.config;

import com.inter3i.sun.api.ota.v1.util.ValidateUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class JobConfig {

    private static Resource resource = new ClassPathResource("/jobconfig.properties");
    /**
     * 所有的缓存名称 ---> 描述的 映射
     */
    private Map<String, String> cacheNameCacheDescMap = new HashMap<>(4);
    /**
     * 根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private Map<String, String> cacheNameDataTableMap = new HashMap<>(4);

    public static void main(String[] args){
        getConfig();
    }

    public static JobConfig getConfig() {

        synchronized (JobConfig.class) {

            //初始化该配置类
            JobConfig tmp = null;
            try {
                tmp = new JobConfig();
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                Iterator it = props.keySet().iterator();
                String key;
                while (it.hasNext()) {
                    key = (String) it.next();
                    if(key.contains("talbename")){
                        tmp.cacheNameCacheDescMap.put(getKeyIn(key), (String) props.get(key));
                    }else if(key.contains("dsname")){
                        tmp.cacheNameDataTableMap.put(getKeyIn(key), (String) props.get(key));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tmp;
        }
    }

    //获取di.cacheTableMap[cache01] 中的[] 中的key
    private static String getKeyIn(String key) {
        int start=key.indexOf(".",4)+1;
        int end=key.indexOf(".",15);
        String rs=key.substring(start, end);
        return rs;
    }

    public Iterator<String> getAllCacheNames() {
        return cacheNameDataTableMap.keySet().iterator();
    }

    public String getDataTableNameBy(final String cacheName) {
        validateCacheNameDataTables(cacheName);
        return cacheNameDataTableMap.get(cacheName);
    }

    private void validateCacheNameDataTables(final String cacheName) {
        if (ValidateUtils.isNullOrEmpt(this.cacheNameDataTableMap)) {
            throw new RuntimeException("cache name <===> dataTable mapping is empety!");
        }
        if (!cacheNameDataTableMap.containsKey(cacheName)) {
            throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
        }
    }

}
