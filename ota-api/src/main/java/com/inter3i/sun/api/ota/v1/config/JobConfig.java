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
     * 入库所有的缓存名称 ---> 描述的 映射
     */
    private static Map<String, String> importCacheNameCacheDescMap = new HashMap<>(4);
    /**
     * 入库根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private static Map<String, String> importCacheNameDataTableMap = new HashMap<>(4);

    /**
     * 分词所有的缓存名称 ---> 描述的 映射
     */
    private static Map<String, String> segmentCacheNameCacheDescMap = new HashMap<>(4);
    /**
     * 分词根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private static Map<String, String> segmentCacheNameDataTableMap = new HashMap<>(4);

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
                    if(key.contains("import")){
                        if(key.contains("talbename")){
                            tmp.importCacheNameCacheDescMap.put(getKeyIn(key), (String) props.get(key));
                        }else if(key.contains("dsname")){
                            tmp.importCacheNameDataTableMap.put(getKeyIn(key), (String) props.get(key));
                        }
                    }
                    if(key.contains("segment")){
                        if(key.contains("talbename")){
                            tmp.segmentCacheNameCacheDescMap.put(getKeyIn(key), (String) props.get(key));
                        }else if(key.contains("dsname")){
                            tmp.segmentCacheNameDataTableMap.put(getKeyIn(key), (String) props.get(key));
                        }
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

    public Iterator<String> getAllCacheNames(String jobName) {
        Iterator<String> rs=null;
        if("import".equals(jobName)){
            rs=importCacheNameDataTableMap.keySet().iterator();
        }
        if("segment".equals(jobName)){
            rs=segmentCacheNameDataTableMap.keySet().iterator();
        }
        return rs;
    }

    public String getDataTableNameBy(String cacheName,String jobName) {

        validateCacheNameDataTables(cacheName,jobName);
        if("import".equals(jobName)) {
            return importCacheNameCacheDescMap.get(cacheName);
        }
        if("segment".equals(jobName)) {
            return segmentCacheNameDataTableMap.get(cacheName);
        }
        return null;
    }

    private void validateCacheNameDataTables(final String cacheName,String jobName) {
        if("import".equals(jobName)){
            if (ValidateUtils.isNullOrEmpt(this.importCacheNameDataTableMap)) {
                throw new RuntimeException("cache name <===> dataTable mapping is empety!");
            }
            if (!importCacheNameDataTableMap.containsKey(cacheName)) {
                throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
            }
        }

        if("segment".equals(jobName)){
            if (ValidateUtils.isNullOrEmpt(this.segmentCacheNameDataTableMap)) {
                throw new RuntimeException("cache name <===> dataTable mapping is empety!");
            }
            if (!segmentCacheNameDataTableMap.containsKey(cacheName)) {
                throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
            }
        }
    }

}
