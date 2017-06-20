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

public class StoreDataConfig {

    private static Resource resource = new ClassPathResource("/storedata.properties");

    private static final String TABLENAME="tablename";

    private static final String DSNAME="dsname";
    /**
     * 数据入库(mongo)时候给每个缓存名称指定数据源
     */
    private static Map<String, String> storeDataCacheNameDsNameMap = new HashMap<>(4);
    /**
     * 数据入库(mongo)时候给每个缓存名称指定talblename
     */
    private static Map<String, String> storeDataCacheNameTableNameMap = new HashMap<>(4);


    public static void main(String[] args) {
        getConfig();
    }

    public static StoreDataConfig getConfig() {

        synchronized (StoreDataConfig.class) {

            //初始化该配置类
            StoreDataConfig tmp = null;
            try {
                tmp = new StoreDataConfig();
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                Iterator it = props.keySet().iterator();
                String key;
                while (it.hasNext()) {
                    key = (String) it.next();
                    if (key.contains(TABLENAME)) {
                        tmp.storeDataCacheNameTableNameMap.put(getKeyIn(key), (String) props.get(key));
                    } else if (key.contains(DSNAME)) {
                        tmp.storeDataCacheNameDsNameMap.put(getKeyIn(key), (String) props.get(key));
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

        int start = key.indexOf(".", 15) + 1;
        int end = key.length();
        String rs = key.substring(start, end);
        return rs;
    }

    public Iterator<String> getAllCacheNames(String sourceParam) {
        Iterator<String> rs = null;
        if (DSNAME.equals(sourceParam)) {
            rs = storeDataCacheNameDsNameMap.keySet().iterator();
        }
        if (TABLENAME.equals(sourceParam)) {
            rs = storeDataCacheNameTableNameMap.keySet().iterator();
        }
        return rs;
    }

    public String getDataTableORSourceName(String cacheName, String sourceParam) {

        validateCacheNameDataTables(cacheName, sourceParam);
        if (DSNAME.equals(sourceParam)) {
            return storeDataCacheNameDsNameMap.get(cacheName);
        }
        if (TABLENAME.equals(sourceParam)) {
            return storeDataCacheNameTableNameMap.get(cacheName);
        }
        return null;
    }

    private void validateCacheNameDataTables(final String cacheName, String sourceParam) {

        if(TABLENAME.equals(sourceParam)){
            if (ValidateUtils.isNullOrEmpt(this.storeDataCacheNameTableNameMap)) {
                throw new RuntimeException("cache name <===> dataTable mapping is empety!");
            }
            if (!storeDataCacheNameTableNameMap.containsKey(cacheName)) {
                throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
            }
        }

        if(DSNAME.equals(sourceParam)){
            if (ValidateUtils.isNullOrEmpt(this.storeDataCacheNameDsNameMap)) {
                throw new RuntimeException("cache name <===> dataTable mapping is empety!");
            }
            if (!storeDataCacheNameDsNameMap.containsKey(cacheName)) {
                throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
            }
        }
    }

}
